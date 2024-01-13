package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class GenerateSuggestedKeywords(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val tokenizeText: TokenizeText,
    private val validateToken: ValidateToken
) {

    suspend operator fun invoke(amount: Int = AMOUNT): List<SuggestedSearch> = withContext(dispatcher) {
        val out = mutableListOf<SuggestedSearch>()

        val textSuggestions = async {
            val textEmbedValues = textEmbeddingDao.getValueConcatAtRandom(amount)
            textEmbedValues?.let {
                tokenizeText(it)
                    .filter { token -> validateToken(token) }
                    .take(2)
                    .map { token ->
                        SuggestedSearch(
                            query = token.text,
                            keywordType = KeywordType.TEXT,
                            searchType = SearchType.PARTIAL
                        )
                    }
                    .flowOn(dispatcher)
                    .toList()
            } ?: emptyList()
        }

        val userSuggestions = async {
            val userEmbedValues = userEmbeddingDao.getValueConcatAtRandom(amount)
            userEmbedValues?.let {
                tokenizeText(it)
                    .filter { token -> validateToken(token) }
                    .take(2)
                    .map { token ->
                        SuggestedSearch(
                            query = token.text,
                            keywordType = KeywordType.TEXT,
                            searchType = SearchType.PARTIAL
                        )
                    }
                    .flowOn(dispatcher)
                    .toList()
            } ?: emptyList()
        }

        val visualSuggestions = async {
            visualEmbeddingDao.getValuesAtRandom(amount)
                .map { value ->
                    SuggestedSearch(
                        query = value,
                        keywordType = KeywordType.IMAGE,
                        searchType = SearchType.FULL
                    )
                }
        }

        awaitAll(textSuggestions, userSuggestions, visualSuggestions).forEach {
            out.addAll(it)
        }

        out
    }

    companion object {
        const val AMOUNT = 4
    }
}