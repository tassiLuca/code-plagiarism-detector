package org.danilopianini.plagiarismdetector.analyzer.representation.token

import kotlinx.serialization.Serializable

/**
 * An interface modeling a token type, e.g. `keyword`, `identifier`...
 * A token type is associated with one or more language-specific constructs.
 * In the Java programming language, for example, the TokenType `loop-stmt`
 * is associated to the following: `ForEachStmt`, `ForStmt`, `WhileStmt`, `DoStmt`.
 */
@Serializable
sealed interface TokenType {
    /**
     * The name of the token type.
     */
    val name: String

    /**
     * The language-specific constructs associated with this type.
     */
    val languageConstructs: Collection<String>
}