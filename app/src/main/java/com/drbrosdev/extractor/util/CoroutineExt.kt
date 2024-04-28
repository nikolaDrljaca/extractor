package com.drbrosdev.extractor.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted

suspend fun <T> CoroutineScope.runCatching(block: suspend  () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (t: CancellationException) {
        throw t
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

val WhileUiSubscribed = SharingStarted.WhileSubscribed(5_000)