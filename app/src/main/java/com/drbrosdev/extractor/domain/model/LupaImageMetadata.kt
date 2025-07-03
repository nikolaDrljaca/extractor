package com.drbrosdev.extractor.domain.model

import java.time.LocalDateTime

data class LupaImageMetadata(
    val mediaImageId: MediaImageId,
    val uri: MediaImageUri,
    val path: String,
    val dateAdded: LocalDateTime,
)