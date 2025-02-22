package com.drbrosdev.extractor.domain.usecase.image

import com.drbrosdev.extractor.domain.model.FtsQuery
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.Token

// Build an FTS query that the sqlite3 FTS engine can execute
class BuildFtsQuery {
    operator fun invoke(params: Params): FtsQuery =
        FtsQuery(
            buildFtsAdaptedQuery(
                tokens = params.tokens,
                searchType = params.searchType,
                keywordType = params.keywordType
            )
        )

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
            SearchType.FULL -> tokens.joinToString(separator = " ") { "text_index:${it.text}" }
            //textIndex:*grey* OR textIndex:*car*
            SearchType.PARTIAL -> tokens.joinToString(separator = " OR ") { "text_index:*${it.text}*" }
        }
        append(out)
    }

    private fun StringBuilder.appendImageQuery(tokens: List<Token>, searchType: SearchType) {
        val out = when (searchType) {
            //visualIndex:grey visualIndex:car
            SearchType.FULL -> tokens.joinToString(separator = " ") { "visual_index:${it.text}" }
            //visualIndex:*grey* OR visualIndex:*car*
            SearchType.PARTIAL -> tokens.joinToString(separator = " OR ") { "visual_index:*${it.text}*" }
        }
        append(out)
    }

    data class Params(
        val tokens: List<Token>,
        val searchType: SearchType,
        val keywordType: KeywordType
    )
}