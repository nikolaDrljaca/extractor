package com.drbrosdev.extractor.framework.mediastore

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.drbrosdev.extractor.domain.model.MediaStoreImage
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class DefaultMediaStoreImageRepository(
    private val dispatcher: CoroutineDispatcher,
    private val contentResolver: ContentResolver
) : MediaStoreImageRepository {

    override suspend fun getAll(): List<MediaStoreImage> {
        return contentResolver.mediaStoreImagesFlow().first()
    }

    override suspend fun getAllIds(): Set<Long> {
        return contentResolver.mediaStoreImagesFlow()
            .first()
            .map { it.mediaImageId }
            .toSet()
    }

    override suspend fun findAllById(ids: List<Long>): List<MediaStoreImage> {
        return contentResolver.runMediaStoreImageQuery(
            selection = "${MediaStore.Images.Media._ID} IN (${ids.joinToString(", ")})"
        )
    }

    override fun findAllByIdAsFlow(ids: List<Long>): Flow<MediaStoreImage> {
        return contentResolver.runMediaStoreImageQueryAsFlow(
            selection = "${MediaStore.Images.Media._ID} IN (${ids.joinToString(", ")})"
        )
    }

    override suspend fun getCount(): Int {
        return contentResolver.getCount()
    }

    override fun getCountAsFlow(): Flow<Int> {
        return contentResolver.mediaStoreImagesFlow()
            .map { it.size }
            .flowOn(dispatcher)
    }

    override suspend fun findByUri(uri: Uri): MediaStoreImage? {
        return contentResolver.findByUri(uri = uri)
    }

    override suspend fun findById(id: Long): MediaStoreImage? {
        return contentResolver.mediaStoreImagesFlow()
            .first()
            .find { it.mediaImageId == id }
    }

    override suspend fun getThumbnails(
        imagesPaths: List<MediaStoreImageRepository.ImagePaths>
    ): List<Bitmap> =
        imagesPaths.map {
            it.generateThumbnail()
        }

    override suspend fun getThumbnail(imagesPaths: MediaStoreImageRepository.ImagePaths): Bitmap =
        withContext(dispatcher) {
            imagesPaths.generateThumbnail()
        }

    private fun MediaStoreImageRepository.ImagePaths.generateThumbnail(): Bitmap {
        val size = 250
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val uri = Uri.parse(Uri.decode(uri))
                contentResolver.loadThumbnail(uri, Size(size, size), null)
            }

            else -> ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(path),
                size,
                size
            )
        }
    }
}
