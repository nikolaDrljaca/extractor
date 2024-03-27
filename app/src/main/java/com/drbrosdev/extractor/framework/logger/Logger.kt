package com.drbrosdev.extractor.framework.logger

import android.annotation.SuppressLint
import android.util.Log
import timber.log.Timber

private val Any.simpleName: String
    get() {
        val name = this::class.simpleName
        if (name != null) {
            return name
        }
        return "Extractor: "
    }

@SuppressLint("LogNotTimber")
fun Any.logInfo(message: String) {
    Log.i(simpleName, message)
}

fun Any.logEvent(message: String) {
    Timber.tag(simpleName).i(message)
}

fun Any.logErrorEvent(
    message: String,
    throwable: Throwable? = null
) {
    Timber.tag(simpleName).e(throwable, message)
}