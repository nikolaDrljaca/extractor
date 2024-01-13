package com.drbrosdev.extractor.ui.components.suggestsearch

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.SuggestedSearch


@Immutable
sealed interface ExtractorSuggestedSearchState {

    data class Content(
        val suggestedSearches: List<SuggestedSearch>
    ) : ExtractorSuggestedSearchState

    data object Loading : ExtractorSuggestedSearchState

    data object Empty : ExtractorSuggestedSearchState
}
