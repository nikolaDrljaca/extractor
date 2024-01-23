package com.drbrosdev.extractor.util

import android.util.Log


fun Any.logInfo(message: String) {
    Log.i(this::class.simpleName, message)
}

fun Any.logDebug(message: String) {
    Log.d(this::class.simpleName, message)
}

fun Any.logError(
    message: String,
    throwable: Throwable? = null
) {
    Log.e(this::class.simpleName, message, throwable)
}

fun Any.logWarn(message: String) {
    Log.w(this::class.simpleName, message)
}