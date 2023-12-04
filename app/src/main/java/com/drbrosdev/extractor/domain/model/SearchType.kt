package com.drbrosdev.extractor.domain.model

enum class SearchType {
    FULL,
    PARTIAL
}

fun SearchType.asString(): String {
    return when (this) {
        SearchType.PARTIAL -> "Partial"
        SearchType.FULL -> "Full"
    }
}
