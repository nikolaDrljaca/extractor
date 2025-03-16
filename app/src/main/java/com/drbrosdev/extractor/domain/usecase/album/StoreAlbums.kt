package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionCollage
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum

class StoreAlbums(
    private val albumRepository: AlbumRepository
) {
    suspend fun execute(
        collages: List<ExtractionCollage>,
        origin: NewAlbum.Origin
    ) {
        val keywordType = when (origin) {
            NewAlbum.Origin.VISUAL_COMPUTED -> KeywordType.IMAGE
            NewAlbum.Origin.TEXT_COMPUTED -> KeywordType.TEXT
            NewAlbum.Origin.USER_GENERATED -> KeywordType.ALL
        }

        collages.forEach {
            val payload = buildNewAlbumPayload(it.extractions, it.keyword)
                .copy(keywordType = keywordType)
            albumRepository.createAlbum(payload)
        }
    }

    private fun buildNewAlbumPayload(
        extractions: List<Extraction>,
        searchTerm: String
    ): NewAlbum {
        val entries = extractions.map {
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