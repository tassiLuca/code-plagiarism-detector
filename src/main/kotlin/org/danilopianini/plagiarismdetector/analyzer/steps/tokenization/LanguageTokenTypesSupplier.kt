package org.danilopianini.plagiarismdetector.analyzer.steps.tokenization

import org.danilopianini.plagiarismdetector.analyzer.representation.token.LanguageTokenTypes

/**
 * A supplier of [LanguageTokenTypes].
 */
interface LanguageTokenTypesSupplier {
    /**
     * The token types object.
     */
    val types: LanguageTokenTypes
}