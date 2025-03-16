package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams

interface SearchImageByQuery {

    suspend fun execute(imageSearchParams: ImageSearchParams): List<Extraction>

}
