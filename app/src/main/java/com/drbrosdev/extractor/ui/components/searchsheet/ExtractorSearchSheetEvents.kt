package com.drbrosdev.extractor.ui.components.searchsheet

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType

sealed interface ExtractorSearchSheetEvents {
    data class OnSearchClick(
        val data: SheetData
    ) : ExtractorSearchSheetEvents

    data class OnDateChange(
        val data: SheetData
    ) : ExtractorSearchSheetEvents

    data class OnChange(
        val data: SheetData
    ) : ExtractorSearchSheetEvents

    data class SheetData(
        val query: String,
        val keywordType: KeywordType,
        val searchType: SearchType,
        val dateRange: DateRange?
    )
}
