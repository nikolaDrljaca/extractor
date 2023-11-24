package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.LabelType

data class ImageSearchQuery(
    val query: String,
    val labelType: LabelType,
    val dateRange: DateRange?
)

