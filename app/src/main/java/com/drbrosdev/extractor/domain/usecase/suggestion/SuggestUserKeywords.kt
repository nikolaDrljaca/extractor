package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.usecase.TokenizeText
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
    suspend operator fun invoke(): List<Embed.User> {
        val input = userEmbeddingDao.getValueConcatAtRandom() ?: return emptyList()

        return withContext(dispatcher) {
            tokenizeText.invoke(input)
                .toList()
                .shuffled()
                .take(8)
                .distinct()
                .map { token -> Embed.User(token.text) }
                .toList()
        }
    }
}

