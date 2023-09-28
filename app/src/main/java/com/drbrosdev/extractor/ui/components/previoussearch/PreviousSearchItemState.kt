package com.drbrosdev.extractor.ui.components.previoussearch

import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.PreviousSearch
import com.drbrosdev.extractor.domain.usecase.LabelType

data class PreviousSearchItemState(
    val text: String,
    val count: Int,
    val labelType: LabelType
)

fun PreviousSearchItemState.drawableResource(): Int {
    return when (labelType) {
        LabelType.ALL -> R.drawable.round_tag_24
        LabelType.TEXT -> R.drawable.round_text_fields_24
        LabelType.IMAGE -> R.drawable.round_image_search_24
    }
}

fun PreviousSearchItemState.toPreviousSearch(): PreviousSearch {
    return PreviousSearch(text, count, labelType)
}

fun PreviousSearch.toItemState(): PreviousSearchItemState {
    return PreviousSearchItemState(
        text = this.query,
        count = this.resultCount,
        labelType = this.labelType
    )
}