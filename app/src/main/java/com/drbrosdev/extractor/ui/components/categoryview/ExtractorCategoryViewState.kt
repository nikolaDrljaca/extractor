package com.drbrosdev.extractor.ui.components.categoryview

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.AlbumPreview

@Immutable
sealed interface ExtractorCategoryViewState {

    data object Loading : ExtractorCategoryViewState

    data object Initial : ExtractorCategoryViewState

    data object Empty: ExtractorCategoryViewState

    data class Content(
        val albums: List<AlbumPreview>,
    ) : ExtractorCategoryViewState
}
