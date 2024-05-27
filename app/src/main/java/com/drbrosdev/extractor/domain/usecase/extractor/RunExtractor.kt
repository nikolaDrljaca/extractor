package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri

interface RunExtractor {

    /**
     * Performs extraction/inference on an media item pointed to by [mediaImageUri].
     *
     * Underlying captured errors are swallowed and null is returned instead.
     */
    suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds?
}
