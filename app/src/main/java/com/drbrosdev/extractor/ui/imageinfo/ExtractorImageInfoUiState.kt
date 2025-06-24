package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.MediaImageId


@Immutable
data class ExtractorImageInfoUiState(
    val mediaImageId: MediaImageId = MediaImageId(0L),
    val userEmbedding: List<UserEmbedUiModel> = emptyList(),
    val visualEmbedding: List<VisualEmbedUiModel> = emptyList(),
)

@Immutable
data class VisualEmbedUiModel(
    val text: String,
    val isChecked: Boolean,
)

@Immutable
data class UserEmbedUiModel(
    val text: String,
    val isChecked: Boolean
)

/////

@Immutable
data class LupaImageDetailState(
    val heading: LupaImageHeading,
    val description: String?,
    val editables: LupaImageEditables
) {
    val hasDescription = description != null
}

// EDITABLES
sealed interface LupaImageEditablesEvents {
    data object OnTextEdit : LupaImageEditablesEvents
    data object OnVisualEdit : LupaImageEditablesEvents
    data object OnUserEdit : LupaImageEditablesEvents
}

@Immutable
data class LupaImageEditables(
    val textEmbed: String,
    val visualEmbeds: Annotations,
    val userEmbeds: Annotations,
    val eventSink: (LupaImageEditablesEvents) -> Unit
)

@Immutable
data class Annotations(
    val embeds: List<String>
) {
    val isEmpty = embeds.isEmpty()
}

// HEADING
@Immutable
data class LupaImageHeading(
    val mediaImageId: Long,
    val uri: String,
    val dateAdded: String,
)
