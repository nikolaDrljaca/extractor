package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.MediaImage


interface ImageSearchByLabel {

    suspend fun search(searchQuery: ImageSearchQuery): List<MediaImage>
}
