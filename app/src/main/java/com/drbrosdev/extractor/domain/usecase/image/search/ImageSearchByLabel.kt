package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage


interface ImageSearchByLabel {

    suspend fun search(query: String, labelType: LabelType): List<MediaImage>
}
