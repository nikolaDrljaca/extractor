package com.drbrosdev.extractor.domain.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import arrow.fx.coroutines.parMap
import com.drbrosdev.extractor.domain.findByUri
import com.drbrosdev.extractor.domain.getCount
import com.drbrosdev.extractor.domain.mediaImagesFlow
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.runMediaImageQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


interface MediaImageRepository {
    suspend fun getAll(): List<MediaImage>

    suspend fun getAllIds(): Set<Long>

    suspend fun findAllById(ids: List<Long>): List<MediaImage>

    suspend fun getCount(): Int

    suspend fun findByUri(uri: Uri): MediaImage?

    suspend fun findById(id: Long): MediaImage?

    suspend fun getThumbnails(imagesPaths: List<MediaImage>): List<Bitmap>
}

class DefaultMediaImageRepository(
    private val dispatcher: CoroutineDispatcher,
    private val contentResolver: ContentResolver
) : MediaImageRepository {

    override suspend fun getAll(): List<MediaImage> {
        return contentResolver.mediaImagesFlow().first()
    }

    override suspend fun getAllIds(): Set<Long> {
        return contentResolver.mediaImagesFlow()
            .first()
            .map { it.mediaImageId }
            .toSet()
    }

    override suspend fun findAllById(ids: List<Long>): List<MediaImage> {
        return contentResolver.runMediaImageQuery(
            selection = "${MediaStore.Images.Media._ID} IN (${ids.joinToString(", ")})"
        )
    }

    override suspend fun getCount(): Int {
        return contentResolver.getCount()
    }

    override suspend fun findByUri(uri: Uri): MediaImage? {
        return contentResolver.findByUri(uri = uri)
    }

    override suspend fun findById(id: Long): MediaImage? {
        return contentResolver.mediaImagesFlow()
            .first()
            .find { it.mediaImageId == id }
    }

    override suspend fun getThumbnails(imagesPaths: List<MediaImage>): List<Bitmap> =
        withContext(dispatcher) {
            val size = 300

            imagesPaths.parMap {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        contentResolver.loadThumbnail(it.uri, Size(size, size), null)

                    else -> ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(it.path),
                        size,
                        size
                    )
                }
            }
        }
}