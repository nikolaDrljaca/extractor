package com.drbrosdev.extractor.ui.usercollage

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.LupaBundle

sealed interface ExtractorUserCollageUiState {

    @Immutable
    data class Content(
        val collages: List<LupaBundle>,
        val onItemClicked: (keyword: String, index: Int) -> Unit,
        val onShare: (keyword: String) -> Unit
    ) : ExtractorUserCollageUiState

    data object Loading : ExtractorUserCollageUiState
}

fun ExtractorUserCollageUiState.findCollageByKeyword(keyword: String) = when (this) {
    is ExtractorUserCollageUiState.Content -> this.collages
        .find { it.keyword.lowercase() == keyword.lowercase() }

    ExtractorUserCollageUiState.Loading -> error("Accessing Collage items outside content state.")
}
