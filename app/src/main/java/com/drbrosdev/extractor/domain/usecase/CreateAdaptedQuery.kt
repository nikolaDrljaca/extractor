package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.domain.model.AdaptedQuery
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.Token

class CreateAdaptedQuery {

    operator fun invoke(params: Params): AdaptedQuery = with(params) {
        AdaptedQuery(
            appendSearchIndexTableNames(
                adaptedQuery = buildFtsAdaptedQuery(tokens, searchType),
                keywordType = keywordType
            )
        )
    }

    private fun buildFtsAdaptedQuery(
        tokens: List<Token>,
        searchType: SearchType
    ) = buildString {
        val separator = when (searchType) {
            SearchType.FULL -> " "
            SearchType.PARTIAL -> "*"
        }

        if (searchType == SearchType.PARTIAL) append("*")

        val words = tokens.joinToString(separator = separator) { it.text }
        append(words)
        append("*")
    }

    private fun appendSearchIndexTableNames(
        adaptedQuery: String,
        keywordType: KeywordType
    ) = buildString {
        when (keywordType) {
            KeywordType.ALL -> append(adaptedQuery)
            KeywordType.TEXT -> append("textIndex:$adaptedQuery OR userIndex:$adaptedQuery")
            KeywordType.IMAGE -> append("visualIndex:$adaptedQuery OR userIndex:$adaptedQuery")
        }
    }

    data class Params(
        val tokens: List<Token>,
        val searchType: SearchType,
        val keywordType: KeywordType
    )
}