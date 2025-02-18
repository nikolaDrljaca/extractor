package com.drbrosdev.extractor.domain.model

import android.net.Uri
import java.time.LocalDateTime

data class MediaStoreImage(
    val mediaImageId: Long,
    val uri: Uri,
    val path: String,
    val dateAdded: LocalDateTime,
    val displayName: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val mimeType: String,
)

fun MediaStoreImage.mediaImageUri() = MediaImageUri(this.uri.toString())

fun MediaStoreImage.mediaImageId() = MediaImageId(this.mediaImageId)
