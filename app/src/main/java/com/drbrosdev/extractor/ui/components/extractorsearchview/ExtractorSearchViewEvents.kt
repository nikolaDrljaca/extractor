package com.drbrosdev.extractor.ui.components.extractorsearchview

import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChipData

sealed interface ExtractorSearchViewEvents {
    data class OnImageLabelFilterChanged(val data: ImageLabelFilterChipData) :
        ExtractorSearchViewEvents

    data class OnQueryChanged(val data: String) : ExtractorSearchViewEvents

    data object OnPerformSearch: ExtractorSearchViewEvents
}
