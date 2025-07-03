package com.drbrosdev.extractor.domain.usecase.suggestion

import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.data.extraction.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.SuggestedSearch
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileSearchSuggestions(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val tokenizeText: TokenizeText,
) {
    suspend operator fun invoke(
        scopes: Collection<SearchSuggestionScope> = SearchSuggestionScope.default
    ): List<SuggestedSearch> = scopes.parMap(
        concurrency = 3,
        context = dispatcher
    ) {
        val source = when (it.keywordType) {
            KeywordType.ALL -> userEmbeddingDao.getValueConcatAtRandom()
            KeywordType.TEXT -> textEmbeddingDao.getValueConcatAtRandom()
            KeywordType.IMAGE -> visualEmbeddingDao.getValuesAtRandom()
                ?.replace(", ", "")
        }

        produceSuggestions(
            input = source,
            size = it.amount,
            keywordType = it.keywordType
        )
    }
        .flatten()

    private suspend fun produceSuggestions(
        input: String?,
        size: Int,
        keywordType: KeywordType
    ): List<SuggestedSearch> {
        if (input == null) return emptyList()

        // Do not perform validation on User created keywords
        val out = when (keywordType) {
            KeywordType.ALL -> tokenizeText.invoke(input)
            else -> tokenizeText.invoke(input)
                .filter { token -> token.isValidSearchToken() }
        }
            .toList()

        val searchType = when (keywordType) {
            KeywordType.ALL -> SearchType.FULL
            else -> SearchType.PARTIAL
        }

        return out
            .shuffled()
            .take(size)
            .distinct() // skip duplicates
            .map { token ->
                SuggestedSearch(
                    query = token.text,
                    keywordType = keywordType,
                    searchType = searchType
                )
            }
    }
}