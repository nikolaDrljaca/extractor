package com.drbrosdev.extractor.domain.repository

import android.content.ContentResolver
import com.drbrosdev.extractor.domain.mediaImagesFlow
import com.drbrosdev.extractor.domain.model.MediaImage
import kotlinx.coroutines.flow.first


interface MediaImageRepository {
    suspend fun getAll(): List<MediaImage>
}

class DefaultMediaImageRepository(
    private val contentResolver: ContentResolver
): MediaImageRepository {

    override suspend fun getAll(): List<MediaImage> {
        return contentResolver.mediaImagesFlow().first()
    }
}