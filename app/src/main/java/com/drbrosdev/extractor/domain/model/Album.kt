package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.domain.model.search.SearchType

data class Album(
    val id: Long,
    val name: String,

    // TODO should be replaced with imageSearchParams
    val keyword: String,
    val searchType: SearchType,
    val keywordType: KeywordType,

    val entries: List<AlbumEntry>
)
