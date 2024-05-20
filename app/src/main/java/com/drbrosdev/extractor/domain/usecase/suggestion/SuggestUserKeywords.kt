package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.domain.usecase.ValidateSuggestedSearchToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

/**
 * Fetch existing user keywords made as tags for images.
 *
 * Values are fetched at random, up to a maximum of 8 values.
 */
class SuggestUserKeywords(
    private val dispatcher: CoroutineDispatcher,
    private val userEmbeddingDao: UserEmbeddingDao,
    private val tokenizeText: TokenizeText,
    private val validateSuggestedSearchToken: ValidateSuggestedSearchToken
) {

    suspend operator fun invoke(): List<Embed.User> {
        val input = userEmbeddingDao.getValueConcatAtRandom() ?: return emptyList()

        val flow = tokenizeText.invoke(input)
            .filter { token -> validateSuggestedSearchToken.invoke(token) }
            .flowOn(dispatcher)

        return withContext(dispatcher) {
            flow
                .toList()
                .shuffled()
                .take(8)
                .distinct()
                .map { token -> Embed.User(token.text) }
                .toList()
        }
    }
}

