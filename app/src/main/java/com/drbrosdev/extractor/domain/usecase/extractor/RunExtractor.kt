package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri

interface RunExtractor {

    suspend fun execute(mediaImageUri: MediaImageUri): Result<ImageEmbeds>
}
