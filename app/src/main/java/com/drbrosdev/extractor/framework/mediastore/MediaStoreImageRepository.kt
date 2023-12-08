package com.drbrosdev.extractor.framework.mediastore

import android.graphics.Bitmap
import android.net.Uri


interface MediaStoreImageRepository {
    suspend fun getAll(): List<MediaStoreImage>

    suspend fun getAllIds(): Set<Long>

    suspend fun findAllById(ids: List<Long>): List<MediaStoreImage>

    suspend fun getCount(): Int

    suspend fun findByUri(uri: Uri): MediaStoreImage?

    suspend fun findById(id: Long): MediaStoreImage?

    suspend fun getThumbnails(imagesPaths: List<ImagePaths>): List<Bitmap>

    suspend fun getThumbnail(imagesPaths: ImagePaths): Bitmap

    data class ImagePaths(val uri: String, val path: String)
}
