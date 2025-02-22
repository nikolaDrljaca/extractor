package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.data.extraction.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class CompileSearchSuggestions(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val tokenizeText: TokenizeText,
) {
    suspend operator fun invoke(): List<SuggestedSearch> = withContext(dispatcher) {
        val textSuggestions = async {
            produceSuggestions(
                textEmbeddingDao.getValueConcatAtRandom(),
                TAKE_TEXT,
                KeywordType.TEXT
            )
        }
        val userSuggestions = async {
            produceSuggestions(
                userEmbeddingDao.getValueConcatAtRandom(),
                TAKE_USER,
                KeywordType.ALL
            )
        }
        val visualSuggestions = async {
            produceSuggestions(
                visualEmbeddingDao.getValuesAtRandom()?.replace(",", " "),
                TAKE_VISUAL,
                KeywordType.IMAGE
            )
        }

        val textOut = textSuggestions.await()
        val userOut = userSuggestions.await()
        val visual = visualSuggestions.await()

        (textOut + userOut + visual)
    }

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
            .flowOn(dispatcher)
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

    companion object {
        private const val TAKE_TEXT = 4
        private const val TAKE_USER = 2
        private const val TAKE_VISUAL = 2
    }
}