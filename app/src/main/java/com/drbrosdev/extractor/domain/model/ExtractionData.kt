package com.drbrosdev.extractor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ExtractionData(
    val extraction: Extraction,
    val textEmbed: Embed.Text,
    val visualEmbeds: List<Embed.Visual>
)