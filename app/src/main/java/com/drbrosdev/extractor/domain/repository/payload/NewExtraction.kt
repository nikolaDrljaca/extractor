package com.drbrosdev.extractor.domain.repository.payload

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import java.time.LocalDateTime

data class NewExtraction(
    val mediaImageId: MediaImageId,
    val extractorImageUri: MediaImageUri,
    val path: String,
    val dateAdded: LocalDateTime,
    val textEmbed: Embed.Text,
    val visualEmbeds: List<Embed.Visual>,
)
