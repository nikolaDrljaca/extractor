package com.drbrosdev.extractor.ui.components.categoryview

import com.drbrosdev.extractor.domain.model.AlbumPreview
import kotlinx.collections.immutable.ImmutableList


sealed interface ExtractorCategoryViewState {
    data object Loading : ExtractorCategoryViewState

    data object Initial : ExtractorCategoryViewState

    data class Content(
        val albums: ImmutableList<AlbumPreview>,
    ) : ExtractorCategoryViewState
}
