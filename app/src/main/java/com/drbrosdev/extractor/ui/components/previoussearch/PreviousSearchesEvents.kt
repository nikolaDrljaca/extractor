package com.drbrosdev.extractor.ui.components.previoussearch

import com.drbrosdev.extractor.domain.model.PreviousSearch


sealed interface PreviousSearchesEvents {
    data class PerformSearch(val query: String) : PreviousSearchesEvents

    data class OnDeleteSearch(val previousSearch: PreviousSearch) : PreviousSearchesEvents
}
