package com.drbrosdev.extractor.ui.search

import com.drbrosdev.extractor.domain.model.MediaImageInfo
import com.drbrosdev.extractor.domain.usecase.LabelType



sealed class ExtractorSearchScreenUiState {
    abstract val labelType: LabelType
    abstract val searchTerm: String
    abstract val initialLabelIndex: Int

    data class Success(
        val images: List<MediaImageInfo>,
        override val searchTerm: String,
        override val labelType: LabelType,
    ) : ExtractorSearchScreenUiState() {
        override val initialLabelIndex: Int
            get() = labelType.toIndex()
    }

    data class Loading(
        override val labelType: LabelType,
        override val searchTerm: String
    ) : ExtractorSearchScreenUiState() {
        override val initialLabelIndex: Int
            get() = labelType.toIndex()
    }

}

private fun LabelType.toIndex(): Int {
    return when (this) {
        LabelType.ALL -> 0
        LabelType.TEXT -> 1
        LabelType.IMAGE -> 2
    }
}
