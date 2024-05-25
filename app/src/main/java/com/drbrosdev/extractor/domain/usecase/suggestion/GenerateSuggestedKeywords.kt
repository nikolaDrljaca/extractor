package com.drbrosdev.extractor.domain.usecase.suggestion

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.domain.usecase.ValidateSuggestedSearchToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class GenerateSuggestedKeywords(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val extractionDao: ExtractionDao,
    private val dataStore: ExtractorDataStore,
    private val tokenizeText: TokenizeText,
    private val validateSuggestedSearchToken: ValidateSuggestedSearchToken
) {

    suspend operator fun invoke(): Either<GenerateSuggestionsError, List<SuggestedSearch>> =
        withContext(dispatcher) {
            when {
                dataStore.getSearchCount() == 0 -> GenerateSuggestionsError.NoSearchesLeft.left()

                extractionDao.getCount() == 0 -> GenerateSuggestionsError.NoExtractionsPresent.left()

                else -> generateSuggestions().right()
            }
        }

    private suspend fun generateSuggestions(): List<SuggestedSearch> = coroutineScope {
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
                .filter { token -> validateSuggestedSearchToken.invoke(token) }
        }

        val searchType = when (keywordType) {
            KeywordType.ALL -> SearchType.FULL
            else -> SearchType.PARTIAL
        }

        return out
            .flowOn(dispatcher)
            .toList()
            .shuffled()
            .take(size)
            .map { token ->
                SuggestedSearch(
                    query = token.text,
                    keywordType = keywordType,
                    searchType = searchType
                )
            }
            .toList()
    }

    companion object {
        private const val TAKE_TEXT = 4
        private const val TAKE_USER = 2
        private const val TAKE_VISUAL = 2
    }
}