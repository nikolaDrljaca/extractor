package com.drbrosdev.extractor.domain.model

import androidx.compose.runtime.Immutable

@Immutable // TODO is a UI model should be mapped there
data class ExtractionData(
    val extraction: Extraction,
    val textEmbed: Embed.Text,
    val visualEmbeds: List<Embed.Visual>
)