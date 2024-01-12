package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType


interface ImageSearchByKeyword {

    suspend fun search(params: Params): List<Extraction>

    data class Params(
        val query: String,
        val keywordType: KeywordType,
        val dateRange: DateRange?,
        val type: SearchType
    )
}
