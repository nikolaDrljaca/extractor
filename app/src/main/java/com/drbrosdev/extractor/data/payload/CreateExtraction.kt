package com.drbrosdev.extractor.data.payload

data class CreateExtraction(
    val mediaImageId: Long,
    val textEmbed: String,
    val visualEmbeds: List<String>,
    val extractorImageUri: String
)
