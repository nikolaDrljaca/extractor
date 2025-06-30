package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.withContext

class SuggestUserKeywords(
    private val dispatcher: CoroutineDispatcher,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val tokenizeText: TokenizeText,
) {
    /**
     * Fetch suggested user keywords for a particular image.
     * Values are fetched at random, up to a maximum of 8 values.
     */
    suspend fun execute(mediaImageId: MediaImageId): List<String> {
        // will return at most 10 words in CSV format
        val input = userEmbeddingDao.getValueConcatAtRandom() ?: return emptyList()
        // fetch existing
        val existing = userEmbeddingDao.findByMediaId(mediaImageId.id)
            ?.let { tokenizeText.invoke(it.value) }
            ?.map { it.text }
            ?.toSet() ?: emptySet()

        return withContext(dispatcher) {
            tokenizeText.invoke(input)
                .map { token -> token.text }
                .filterNot { existing.contains(it) }
                .toList()
                .shuffled()
                .take(8)
                .distinct()
        }
    }
}

