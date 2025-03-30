package com.drbrosdev.extractor.domain.model

data class ExtractionBundle(
    val keyword: String,
    val extractions: List<Extraction>
)