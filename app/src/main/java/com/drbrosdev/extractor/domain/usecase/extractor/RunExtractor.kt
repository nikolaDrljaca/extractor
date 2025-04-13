package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri

interface RunExtractor {

    /**
     * Performs extraction/inference on an media item pointed to by [mediaImageUri].
     *
     * For any failed extractions, null is returned.
     */
    suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds?

}
