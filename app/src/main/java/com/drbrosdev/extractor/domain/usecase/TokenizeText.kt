package com.drbrosdev.extractor.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.regex.Pattern
import kotlin.reflect.KProperty

class TokenizeText(
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(text: String): Flow<Token> = flow {
        val prepared = prepare(text)

        val pattern = Pattern.compile("\\b\\w+\\b") // Matches word boundaries
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

@JvmInline
value class Token(val text: String) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return text;
    }
}