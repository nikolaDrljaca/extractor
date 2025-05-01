package com.drbrosdev.extractor.framework.logger

import timber.log.Timber

private const val simpleName: String = "LupaLog"

fun logEvent(message: String) {
    Timber.tag(simpleName).i(message)
}

fun logErrorEvent(
    message: String,
    throwable: Throwable? = null
) {
    val out = buildString {
        appendLine(message)
        throwable?.let { append(it.message) }
    }
    Timber.tag(simpleName).wtf(message = out)
}