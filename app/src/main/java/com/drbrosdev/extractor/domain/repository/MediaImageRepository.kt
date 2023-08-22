package com.drbrosdev.extractor.domain.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.drbrosdev.extractor.domain.findByUri
import com.drbrosdev.extractor.domain.getCount
import com.drbrosdev.extractor.domain.mediaImagesFlow
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.model.MediaImageInfo
import com.drbrosdev.extractor.domain.runImageQuery
import kotlinx.coroutines.flow.first


interface MediaImageRepository {
    suspend fun getAll(): List<MediaImage>

    suspend fun findAllById(ids: List<Long>): List<MediaImage>

    suspend fun getCount(): Int

    suspend fun findByUri(uri: Uri): MediaImageInfo?
}

class DefaultMediaImageRepository(
    private val contentResolver: ContentResolver
) : MediaImageRepository {

    override suspend fun getAll(): List<MediaImage> {
        return contentResolver.mediaImagesFlow().first()
    }

    override suspend fun findAllById(ids: List<Long>): List<MediaImage> {
        val out = contentResolver.runImageQuery(
            selection = "${MediaStore.Images.Media._ID} IN (${ids.joinToString(", ")})"
        )
        return out
    }

    override suspend fun getCount(): Int {
        return contentResolver.getCount()
    }

    override suspend fun findByUri(uri: Uri): MediaImageInfo? {
        return contentResolver.findByUri(uri = uri)
    }
}