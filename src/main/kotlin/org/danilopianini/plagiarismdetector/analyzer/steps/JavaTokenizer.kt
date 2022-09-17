package org.danilopianini.plagiarismdetector.analyzer.steps

import com.charleskorn.kaml.Yaml
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.TreeVisitor
import kotlinx.serialization.builtins.ListSerializer
import org.danilopianini.plagiarismdetector.analyzer.representation.token.Token
import org.danilopianini.plagiarismdetector.analyzer.representation.token.TokenImpl
import org.danilopianini.plagiarismdetector.analyzer.representation.token.TokenTypeImpl
import org.danilopianini.plagiarismdetector.analyzer.representation.token.languages.JavaTokenTypes

/**
 * A Java source file tokenizer.
 */
class JavaTokenizer : StepHandler<CompilationUnit, Sequence<Token>> {
    override fun process(input: CompilationUnit): Sequence<Token> {
        val tokenizer = TokenizerTreeVisitor()
        tokenizer.visitPreOrder(input)
        return tokenizer.sourceCodeToken
    }

    /**
     * A token generator.
     */
    private class TokenizerTreeVisitor : TreeVisitor() {
        companion object {
            private const val CONFIG_FILE_NAME = "java-token-types.yml"
        }
        private val javaTokenTypes: JavaTokenTypes
        private val tokens = mutableListOf<Token>()

        /**
         * Sequence of [Token] extracted visiting the source AST.
         */
        val sourceCodeToken: Sequence<Token>
            get() {
                val result = tokens.toList()
                tokens.clear()
                return result.asSequence()
            }

        init {
            val configFile = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME)!!
            val tokenTypes = Yaml.default.decodeFromStream(ListSerializer(TokenTypeImpl.serializer()), configFile)
            javaTokenTypes = JavaTokenTypes(tokenTypes.asSequence())
        }

        private fun tokenize(node: Node?) {
            require(node != null)
            val tokenTypeName = node::class.java.simpleName
            if (javaTokenTypes.isValidToken(tokenTypeName)) {
                tokens.add(
                    TokenImpl(
                        node.begin.get().line,
                        node.begin.get().column,
                        javaTokenTypes.getTokenTypeBy(tokenTypeName)
                    )
                )
            }
        }

        override fun process(node: Node?) {
            tokenize(node)
        }
    }
}