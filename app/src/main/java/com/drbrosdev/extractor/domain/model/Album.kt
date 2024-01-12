package com.drbrosdev.extractor.domain.model

data class Album(
    val id: Long,
    val name: String,
    val keyword: String,
    val searchType: SearchType,
    val labelType: LabelType,
    val entries: List<AlbumEntry>
)

