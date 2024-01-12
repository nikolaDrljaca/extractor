package com.drbrosdev.extractor.domain.repository.payload

import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType

data class NewAlbum(
    val keyword: String,
    val name: String,
    val searchType: SearchType,
    val labelType: LabelType,
    val origin: Origin,
    val entries: List<Entry>
) {
    data class Entry(
        val uri: MediaImageUri,
        val id: MediaImageId
    )

    enum class Origin {
        VISUAL_COMPUTED,
        TEXT_COMPUTED,
        USER_GENERATED
    }
}
