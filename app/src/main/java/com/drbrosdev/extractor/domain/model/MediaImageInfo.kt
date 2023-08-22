package com.drbrosdev.extractor.domain.model

data class MediaImageInfo(
    val path: String,
    val dateAdded: String,
    val displayName: String,
    val id: Long,
    val width: Int,
    val height: Int,
    val size: Long
)