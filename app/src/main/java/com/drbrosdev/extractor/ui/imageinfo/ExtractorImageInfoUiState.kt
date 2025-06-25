package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Immutable

@Immutable
data class LupaImageInfoState(
    val heading: LupaImageHeaderState,
    val description: String?,
    val editables: LupaImageEditablesState
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
data class LupaImageEditablesState(
    val textEmbed: String,
    val visualEmbeds: LupaImageAnnotationsState,
    val userEmbeds: LupaImageAnnotationsState,
    val eventSink: (LupaImageEditablesEvents) -> Unit
)

@Immutable
data class LupaImageAnnotationsState(
    val embeds: List<String>
) {
    val isEmpty = embeds.isEmpty()
}

// HEADING
@Immutable
data class LupaImageHeaderState(
    val mediaImageId: Long,
    val uri: String,
    val dateAdded: String,
)
