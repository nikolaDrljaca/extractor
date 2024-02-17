package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
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
    private val validateSuggestedSearchToken: ValidateSuggestedSearchToken
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
            visualEmbeddingDao.getValuesAtRandom(TAKE_VISUAL)
                .map { value ->
                    SuggestedSearch(
                        query = value,
                        keywordType = KeywordType.IMAGE,
                        searchType = SearchType.PARTIAL
                    )
                }
        }

        val textOut = textSuggestions.await()
        val userOut = userSuggestions.await()
        val visual = visualSuggestions.await()

        textOut + userOut + visual
    }

    private suspend fun produceSuggestions(input: String?, size: Int, keywordType: KeywordType) =
        input?.let {
            this.tokenizeText(it).filter { token -> this.validateSuggestedSearchToken(token) }
                .take(size)
                .map { token ->
                    SuggestedSearch(
                        query = token.text,
                        keywordType = keywordType,
                        searchType = SearchType.PARTIAL
                    )
                }
                .flowOn(dispatcher)
                .toList()
        } ?: emptyList()

    companion object {
        private const val TAKE_TEXT = 4
        private const val TAKE_USER = 2
        private const val TAKE_VISUAL = 2
    }
}