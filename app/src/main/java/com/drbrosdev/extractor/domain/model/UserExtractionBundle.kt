package com.drbrosdev.extractor.domain.model

data class UserExtractionBundle(
    val userEmbed: String,
    val extractions: List<Extraction>
)
