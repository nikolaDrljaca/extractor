package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.SuggestedSearch

sealed interface SuggestedSearchUiModel {
    data object Loading: SuggestedSearchUiModel

    data object Empty: SuggestedSearchUiModel

    @Immutable
    data class Content(
        val onSuggestionClick: (SuggestedSearch) -> Unit,
        val suggestions: List<SuggestedSearch>
    ): SuggestedSearchUiModel
}