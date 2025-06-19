package com.drbrosdev.extractor.domain.model

// ImageEmbeds is this
data class LupaImageAnnotations(
    val textEmbed: String,
    val visualEmbeds: List<String>,
    val userEmbeds: List<String>
)