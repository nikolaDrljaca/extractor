package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.SearchType


interface ImageSearchByLabel {

    suspend fun search(params: Params): List<Extraction>

    data class Params(
        val query: String,
        val labelType: LabelType,
        val dateRange: DateRange?,
        val type: SearchType
    )
}
