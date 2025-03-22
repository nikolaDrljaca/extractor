package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.SuggestedSearch

sealed interface SuggestedSearchState {
    data object Loading: SuggestedSearchState

    data object Empty: SuggestedSearchState

    @Immutable
    data class Content(
        val onSuggestionClick: (SuggestedSearch) -> Unit,
        val suggestions: List<SuggestedSearch>
    ): SuggestedSearchState
}