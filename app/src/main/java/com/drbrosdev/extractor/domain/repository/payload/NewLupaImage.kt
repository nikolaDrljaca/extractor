package com.drbrosdev.extractor.domain.repository.payload

import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import java.time.LocalDateTime

data class NewLupaImage(
    val mediaImageId: MediaImageId,
    val extractorImageUri: MediaImageUri,
    val path: String,
    val dateAdded: LocalDateTime,
    val textEmbed: String,
    val visualEmbeds: List<String>,
)
