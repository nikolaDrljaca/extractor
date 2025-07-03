package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.domain.model.KeywordType

data class SearchSuggestionScope(
    val amount: Int,
    val keywordType: KeywordType
) {
    companion object {
        const val TAKE_TEXT = 4
        const val TAKE_USER = 2
        const val TAKE_VISUAL = 2

        val default = buildSuggestionScope {
            text()
            user()
            visual()
        }
    }
}