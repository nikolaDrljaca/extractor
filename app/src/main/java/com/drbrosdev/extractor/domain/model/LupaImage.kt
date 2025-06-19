package com.drbrosdev.extractor.domain.model

// ExtractionData can be this
data class LupaImage(
    val metadata: LupaImageMetadata,
    val annotations: LupaImageAnnotations
)