package com.drbrosdev.extractor.domain.model

import android.net.Uri

data class MediaImageInfo(
    val mediaImageId: Long,
    val uri: Uri,
    val path: String,
    val dateAdded: String,
    val displayName: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val mimeType: String,
)