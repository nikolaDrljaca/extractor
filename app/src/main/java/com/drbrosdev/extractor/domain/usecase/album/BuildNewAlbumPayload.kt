package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum

class BuildNewAlbumPayload {
    operator fun invoke(
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
            searchType = SearchType.FULL,
            keywordType = KeywordType.IMAGE,
            entries = entries,
            origin = NewAlbum.Origin.VISUAL_COMPUTED
        )
    }
}