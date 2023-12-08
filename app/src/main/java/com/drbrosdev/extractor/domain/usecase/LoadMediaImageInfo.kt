package com.drbrosdev.extractor.domain.usecase

import android.net.Uri
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImage
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class LoadMediaImageInfo(
    private val dispatcher: CoroutineDispatcher,
    private val mediaStoreImageRepository: MediaStoreImageRepository
) {

    suspend operator fun invoke(uri: Uri): MediaStoreImage? {
        return withContext(dispatcher) {
            mediaStoreImageRepository.findByUri(uri)
        }
    }
}