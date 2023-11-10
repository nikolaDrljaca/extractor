package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.data.entity.PreviousSearchEntity


data class PreviousSearch(
    val query: String,
    val resultCount: Int,
    val labelType: LabelType
)

fun PreviousSearchEntity.toPreviousSearch(): PreviousSearch {
    return PreviousSearch(query, resultCount, labelType)
}

fun PreviousSearch.toEntity(): PreviousSearchEntity {
    return PreviousSearchEntity(query, resultCount, labelType)
}
