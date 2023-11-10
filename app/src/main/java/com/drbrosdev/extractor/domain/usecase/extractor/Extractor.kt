package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.MediaImage

interface Extractor {
    suspend fun execute(mediaImage: MediaImage)
}
