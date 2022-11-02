package org.danilopianini.plagiarismdetector.core.analyzer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import org.danilopianini.plagiarismdetector.core.analyzer.technique.tokenization.java.JavaTokenizationAnalyzer
import java.io.File

class TokenizationAnalyzerTest : FunSpec() {

    private val sourceFile = File(ClassLoader.getSystemResource(FILE_NAME).toURI())
    private val analyzer = JavaTokenizationAnalyzer()

    init {
        test("Processing phase should remove imports and package declarations, equals and hashcode functions") {
            val result = JavaTokenizationAnalyzer()(sourceFile).representation.toList()
            result.shouldNotBeEmpty()
            result.forEach {
                it.line shouldBeInRange IntRange(
                    FIRST_LINE_CLASS,
                    LAST_LINE_CLASS_WITHOUT_EQUALS_AND_HASHCODE
                )
            }
        }

        test("Tokenizing source code should return a tokenized representation of source code") {
            val result = analyzer(sourceFile)
            result.sourceFile.path shouldBe sourceFile.path
            result.representation.count() shouldBeExactly EXPECTED_TOKENS_NUMBER
        }
    }

    companion object {
        private const val FILE_NAME = "TestAnalyzer.java"
        private const val FIRST_LINE_CLASS = 6
        private const val LAST_LINE_CLASS_WITHOUT_EQUALS_AND_HASHCODE = 16
        private const val EXPECTED_TOKENS_NUMBER = 14
    }
}
