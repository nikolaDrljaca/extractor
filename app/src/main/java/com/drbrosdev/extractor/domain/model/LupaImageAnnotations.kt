package com.drbrosdev.extractor.domain.model

data class LupaImageAnnotations(
    val textEmbed: String,
    val descriptionEmbed: String,
    val visualEmbeds: List<String>,
    val userEmbeds: List<String>
)

enum class AnnotationType {
    TEXT,
    VISUAL,
    USER
}