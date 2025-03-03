package com.drbrosdev.extractor.domain.model

data class ExtractionCollage(
    val keyword: String,
    val extractions: List<Extraction>
)