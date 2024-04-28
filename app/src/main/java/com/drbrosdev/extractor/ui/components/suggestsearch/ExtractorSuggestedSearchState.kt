package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.SuggestedSearch


sealed interface ExtractorSuggestedSearchState {

    @Immutable
    data class Content(
        val onSuggestionClick: (SuggestedSearch) -> Unit,
        val suggestedSearches: List<SuggestedSearch>,
    ) : ExtractorSuggestedSearchState

    data object Loading : ExtractorSuggestedSearchState

    data class Empty(
        val onStartSync: () -> Unit
    ) : ExtractorSuggestedSearchState
}
