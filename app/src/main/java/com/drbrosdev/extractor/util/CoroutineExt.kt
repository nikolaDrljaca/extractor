package com.drbrosdev.extractor.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope

suspend fun <T> CoroutineScope.runCatching(block: suspend  () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (t: CancellationException) {
        throw t
    } catch (e: Throwable) {
        Result.failure(e)
    }
}