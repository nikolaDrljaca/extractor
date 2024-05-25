package com.drbrosdev.extractor.ui.usercollage

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.UserCollage

sealed interface ExtractorUserCollageUiState {

    @Immutable
    data class Content(
        val collages: List<UserCollage>,
        val onItemClicked: (keyword: String, index: Int) -> Unit,
        val onShare: (keyword: String) -> Unit
    ) : ExtractorUserCollageUiState

    data object Loading : ExtractorUserCollageUiState
}

fun ExtractorUserCollageUiState.findCollageByKeyword(keyword: String) = when (this) {
    is ExtractorUserCollageUiState.Content -> this.collages
        .find { it.userEmbed.lowercase() == keyword.lowercase() }

    ExtractorUserCollageUiState.Loading -> error("Accessing Collage items outside content state.")
}
