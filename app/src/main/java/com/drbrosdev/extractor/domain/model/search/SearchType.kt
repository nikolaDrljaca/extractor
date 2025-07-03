package com.drbrosdev.extractor.domain.model.search

import com.drbrosdev.extractor.R

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

fun SearchType.stringRes(): Int = when (this) {
    SearchType.FULL -> R.string.search_type_full
    SearchType.PARTIAL -> R.string.search_type_partial
}
