package com.drbrosdev.extractor.util

import com.drbrosdev.extractor.framework.logger.logEvent

fun Any.panic(message: Any): Nothing {
    logEvent(message.toString())
    throw IllegalStateException(message.toString())
}