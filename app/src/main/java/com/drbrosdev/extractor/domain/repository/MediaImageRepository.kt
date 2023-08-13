package com.drbrosdev.extractor.domain.repository

import android.content.ContentResolver
import android.provider.MediaStore
import com.drbrosdev.extractor.domain.mediaImagesFlow
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.runImageQuery
import kotlinx.coroutines.flow.first


interface MediaImageRepository {
    suspend fun getAll(): List<MediaImage>

    suspend fun getAllById(ids: List<Long>): List<MediaImage>
}

class DefaultMediaImageRepository(
    private val contentResolver: ContentResolver
): MediaImageRepository {

    override suspend fun getAll(): List<MediaImage> {
        return contentResolver.mediaImagesFlow().first()
    }

    override suspend fun getAllById(ids: List<Long>): List<MediaImage> {
        val out = contentResolver.runImageQuery(
            selection = "${MediaStore.Images.Media._ID} IN (${ids.joinToString(", ")})"
        )
        return out
    }
}