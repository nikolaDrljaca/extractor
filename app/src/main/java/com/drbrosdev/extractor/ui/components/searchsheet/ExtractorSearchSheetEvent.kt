package com.drbrosdev.extractor.ui.components.searchsheet

import com.drbrosdev.extractor.domain.model.ImageSearchParams

sealed interface ExtractorSearchSheetEvent {
    data class OnSearch(
        val params: ImageSearchParams
    ) : ExtractorSearchSheetEvent

    data class OnDateChange(
        val params: ImageSearchParams
    ) : ExtractorSearchSheetEvent

    data class OnChange(
        val params: ImageSearchParams
    ) : ExtractorSearchSheetEvent
}
