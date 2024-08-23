package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.domain.model.AdaptedQuery
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.Token

class CreateAdaptedQuery {

    operator fun invoke(params: Params): AdaptedQuery = with(params) {
        val query = buildFtsAdaptedQuery(
            tokens = tokens,
            searchType = searchType,
            keywordType = keywordType
        )
        AdaptedQuery(query)
    }

    private fun buildFtsAdaptedQuery(
        tokens: List<Token>,
        searchType: SearchType,
        keywordType: KeywordType
    ) = buildString {
        when (keywordType) {
            KeywordType.ALL -> appendAllQuery(tokens, searchType)
            KeywordType.TEXT -> appendTextQuery(tokens, searchType)
            KeywordType.IMAGE -> appendImageQuery(tokens, searchType)
        }
    }

    private fun StringBuilder.appendAllQuery(tokens: List<Token>, searchType: SearchType) {
        val out = when (searchType) {
            SearchType.FULL -> tokens.joinToString(separator = " ") { it.text }
            // grey car -> *grey* OR *car*
            SearchType.PARTIAL -> tokens.joinToString(separator = " OR ") { "*${it.text}*" }
        }
        append(out)
    }

    private fun StringBuilder.appendTextQuery(tokens: List<Token>, searchType: SearchType) {
        val out = when (searchType) {
            //textIndex:grey textIndex:car
            SearchType.FULL -> tokens.joinToString(separator = " ") { "textIndex:${it.text}" }
            //textIndex:*grey* OR textIndex:*car*
            SearchType.PARTIAL -> tokens.joinToString(separator = " OR ") { "textIndex:*${it.text}*" }
        }
        append(out)
    }

    private fun StringBuilder.appendImageQuery(tokens: List<Token>, searchType: SearchType) {
        val out = when (searchType) {
            //visualIndex:grey visualIndex:car
            SearchType.FULL -> tokens.joinToString(separator = " ") { "visualIndex:${it.text}" }
            //visualIndex:*grey* OR visualIndex:*car*
            SearchType.PARTIAL -> tokens.joinToString(separator = " OR ") { "visualIndex:*${it.text}*" }
        }
        append(out)
    }

    data class Params(
        val tokens: List<Token>,
        val searchType: SearchType,
        val keywordType: KeywordType
    )
}