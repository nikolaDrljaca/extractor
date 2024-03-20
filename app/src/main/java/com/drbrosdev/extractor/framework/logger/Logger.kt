package com.drbrosdev.extractor.framework.logger

import timber.log.Timber

private val Any.simpleName: String
    get() {
        val name = this::class.simpleName
        if (name != null) {
            return name
        }
        return "Extractor: "
    }

fun Any.logInfo(message: String) {
    Timber.tag(simpleName).i(message)
}

fun Any.logInfo(value: Any?) {
    Timber.tag(simpleName).i(value.toString())
}

fun Any.logDebug(message: String) = Timber.tag(simpleName).d(message)

fun Any.logError(
    message: String,
    throwable: Throwable? = null
) {
    Timber.tag(simpleName).e(throwable, message)
}

fun Any.logWarn(message: String) {
    Timber.tag(simpleName).w(message)
}
