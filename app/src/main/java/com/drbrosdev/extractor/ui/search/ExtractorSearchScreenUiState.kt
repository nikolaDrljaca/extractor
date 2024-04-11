package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Immutable
import arrow.core.Either
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


sealed class ExtractorSearchScreenUiState {

    @Immutable
    data class Content(
        val images: List<Extraction>,
    ) : ExtractorSearchScreenUiState()

    data object Loading : ExtractorSearchScreenUiState()

    data object StillIndexing : ExtractorSearchScreenUiState()

    data class ShowSuggestions(
        val suggestedSearchState: ExtractorSuggestedSearchState
    ) : ExtractorSearchScreenUiState()

    data object NoSearchesLeft : ExtractorSearchScreenUiState()

    data object Empty : ExtractorSearchScreenUiState()
}

fun MutableStateFlow<ExtractorSearchScreenUiState>.createFrom(
    mediaImages: List<Extraction>,
) = update {
    when {
        mediaImages.isEmpty() -> ExtractorSearchScreenUiState.Empty
        else -> ExtractorSearchScreenUiState.Content(mediaImages)
    }
}

fun MutableStateFlow<ExtractorSearchScreenUiState>.createFrom(
    mediaImages: Either<Unit, List<Extraction>>,
) = update {
    mediaImages.fold(
        ifRight = { extractions ->
            when {
                extractions.isEmpty() -> ExtractorSearchScreenUiState.Empty
                else -> ExtractorSearchScreenUiState.Content(extractions)
            }
        },
        ifLeft = {
            ExtractorSearchScreenUiState.NoSearchesLeft
        }
    )
}

fun ExtractorSearchScreenUiState.getImages(): List<Extraction> {
    return when (this) {
        is ExtractorSearchScreenUiState.Content -> this.images
        else -> error("Accessing image list outside of Success state.")
    }
}

sealed interface SheetContent {
    data object SearchView : SheetContent

    data object MultiselectBar : SheetContent
}