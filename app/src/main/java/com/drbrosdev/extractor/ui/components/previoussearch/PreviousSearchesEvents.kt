package com.drbrosdev.extractor.ui.components.previoussearch

import com.drbrosdev.extractor.domain.usecase.LabelType


sealed interface PreviousSearchesEvents {
    data class PerformSearch(val query: String, val labelType: LabelType) : PreviousSearchesEvents

    data class OnDeleteSearch(val previousSearch: PreviousSearchItemState) : PreviousSearchesEvents
}
