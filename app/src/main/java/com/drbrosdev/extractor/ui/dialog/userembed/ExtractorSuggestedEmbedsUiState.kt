package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel

sealed interface ExtractorSuggestedEmbedsUiState {
    data object Loading : ExtractorSuggestedEmbedsUiState

    data object Empty : ExtractorSuggestedEmbedsUiState

    @Immutable
    data class Content(
        val suggestions: List<UserEmbedUiModel>
    ) : ExtractorSuggestedEmbedsUiState
}


fun ExtractorSuggestedEmbedsUiState.getSuggestions(): List<UserEmbedUiModel> =
    when (this) {
        is ExtractorSuggestedEmbedsUiState.Content -> this.suggestions
        is ExtractorSuggestedEmbedsUiState.Empty -> emptyList()
        else -> error("Accessing suggestions outside of Content state.")
    }

