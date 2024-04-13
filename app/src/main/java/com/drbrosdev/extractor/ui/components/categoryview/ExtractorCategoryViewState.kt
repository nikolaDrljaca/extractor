package com.drbrosdev.extractor.ui.components.categoryview

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.AlbumPreview

sealed class ExtractorCategoryViewState {
    abstract val isLoading: Boolean

    data class Initial(
        override val isLoading: Boolean = false
    ) : ExtractorCategoryViewState()

    data class StillIndexing(
        override val isLoading: Boolean = false
    ) : ExtractorCategoryViewState()

    @Immutable
    data class Content(
        val albums: List<AlbumPreview>,
        override val isLoading: Boolean
    ) : ExtractorCategoryViewState()
}
