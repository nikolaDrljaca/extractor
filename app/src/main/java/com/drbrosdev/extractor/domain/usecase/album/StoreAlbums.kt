package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.LupaBundle
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum

class StoreAlbums(
    private val albumRepository: AlbumRepository
) {
    suspend fun execute(
        collages: List<LupaBundle>,
        origin: NewAlbum.Origin
    ) {
        val keywordType = when (origin) {
            NewAlbum.Origin.VISUAL_COMPUTED -> KeywordType.IMAGE
            NewAlbum.Origin.TEXT_COMPUTED -> KeywordType.TEXT
            NewAlbum.Origin.USER_GENERATED -> KeywordType.ALL
        }

        collages.forEach {
            val payload = buildNewAlbumPayload(it.images, it.keyword)
                .copy(keywordType = keywordType)
            albumRepository.createAlbum(payload)
        }
    }

    private fun buildNewAlbumPayload(
        lupaImageMetadata: List<LupaImageMetadata>,
        searchTerm: String
    ): NewAlbum {
        val entries = lupaImageMetadata.map {
            NewAlbum.Entry(
                uri = it.uri,
                id = it.mediaImageId
            )
        }
        return NewAlbum(
            keyword = searchTerm,
            name = searchTerm,
            searchType = SearchType.PARTIAL,
            keywordType = KeywordType.IMAGE,
            entries = entries,
            origin = NewAlbum.Origin.VISUAL_COMPUTED
        )
    }
}