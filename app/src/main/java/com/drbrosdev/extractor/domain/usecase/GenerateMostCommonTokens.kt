package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.domain.model.Token
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn

class GenerateMostCommonTokens(
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(tokens: List<Token>, amount: Int = 7): Flow<Token> {
        return tokens
            .groupingBy { it.text.lowercase() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { Token(it.key) }
            .take(amount)
            .asFlow()
            .flowOn(dispatcher)
    }
}