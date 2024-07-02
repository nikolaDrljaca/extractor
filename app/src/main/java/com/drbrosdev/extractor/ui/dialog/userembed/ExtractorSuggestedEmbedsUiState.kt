package com.drbrosdev.extractor.ui.dialog.userembed

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.ui.imageinfo.UserEmbedUiModel
import com.drbrosdev.extractor.util.panic

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

fun ExtractorSuggestedEmbedsUiState.getSuggestionsExcluding(value: String) =
    when (this) {
        is ExtractorSuggestedEmbedsUiState.Content ->
            suggestions
                .filter { it.text != value }

        else -> panic("Accessing user embed suggestions outside of Content state.")
    }

