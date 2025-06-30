package com.drbrosdev.extractor.ui.imageinfo

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.util.asFormatDate

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
) {
    val isTextBlank = textEmbed.isBlank()
}

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
) {
    companion object {
        fun fromMetadata(metadata: LupaImageMetadata) = LupaImageHeaderState(
            mediaImageId = metadata.mediaImageId.id,
            uri = metadata.uri.uri,
            dateAdded = metadata.dateAdded.asFormatDate()
        )
    }
}

