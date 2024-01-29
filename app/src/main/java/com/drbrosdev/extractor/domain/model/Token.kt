package com.drbrosdev.extractor.domain.model

import kotlin.reflect.KProperty

@JvmInline
value class Token(val text: String) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return text;
    }
}
