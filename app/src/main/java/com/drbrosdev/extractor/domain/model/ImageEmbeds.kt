package com.drbrosdev.extractor.domain.model

data class ImageEmbeds(
    val textEmbed: Embed.Text,
    val visualEmbeds: List<Embed.Visual>,
    val userEmbeds: List<Embed.User>
)