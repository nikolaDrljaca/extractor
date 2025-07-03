package com.drbrosdev.extractor.domain.usecase.token

import com.drbrosdev.extractor.domain.model.Token
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.regex.Pattern

class TokenizeText(
    private val dispatcher: CoroutineDispatcher
) {
    private val wordPattern = Regex("\\b\\w+(?:'\\w+)?\\b")

    operator fun invoke(text: String): Flow<Token> = flow {
        val prepared = prepare(text)

        val pattern = Pattern.compile(wordPattern.pattern)
        val matcher = pattern.matcher(prepared)

        while (matcher.find()) {
            val token = Token(matcher.group())
            emit(token)
        }
    }.flowOn(dispatcher)

    private fun prepare(input: String): String {
        return input
            .trim()
            .replace(Regex("\\s+"), " ")
            .lowercase()
    }
}
