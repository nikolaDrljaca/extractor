package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class SuggestUserKeywords(
    private val dispatcher: CoroutineDispatcher,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val tokenizeText: TokenizeText,
) {
    /**
     * Fetch existing user keywords made as tags for images.
     * Values are fetched at random, up to a maximum of 8 values.
     */
    suspend operator fun invoke(): List<String> {
        // will return at most 10 words in CSV format
        val input = userEmbeddingDao.getValueConcatAtRandom() ?: return emptyList()

        return withContext(dispatcher) {
            tokenizeText.invoke(input)
                .toList()
                .shuffled()
                .take(8)
                .distinct()
                .map { token -> token.text }
        }
    }
}

