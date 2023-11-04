package com.drbrosdev.extractor.ui.result

import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.LabelType



sealed class SearchResultUiState {
    abstract val labelType: LabelType
    abstract val searchTerm: String
    abstract val initialLabelIndex: Int

    data class Success(
        val images: List<MediaImage>,
        override val searchTerm: String,
        override val labelType: LabelType,
    ) : SearchResultUiState() {
        override val initialLabelIndex: Int
            get() = labelType.toIndex()
    }

    data class Loading(
        override val labelType: LabelType,
        override val searchTerm: String
    ) : SearchResultUiState() {
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
