package com.drbrosdev.extractor.domain.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val keyword: String,
    val searchType: SearchType,
    val keywordType: KeywordType,
    val entries: List<AlbumEntry>
)

fun Album.toPreview(): AlbumPreview {
    val thumbnail = when {
        entries.isNotEmpty() -> entries.first().uri
        else -> MediaImageUri(Uri.EMPTY.toString())
    }

    return AlbumPreview(
        id = this.id,
        name = this.name,
        thumbnail = thumbnail
    )
}
