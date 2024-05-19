package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/*
TODO: Migrate to TextFieldState(2) when it goes stable, or at least gets Material TextField support.
 */
@Stable
class ExtractorTextFieldState(
    initialText: String = "",
) {
    var textValue by mutableStateOf(initialText)
        private set

    fun updateTextValue(value: String) {
        textValue = value
    }

    companion object {
        val Saver = run {
            val textKey = "text_key_embed"
            mapSaver(
                save = { mapOf(textKey to it.textValue) },
                restore = {
                    ExtractorTextFieldState(
                        initialText = it[textKey] as String,
                    )
                }
            )
        }
    }
}

@Composable
fun rememberEmbeddingsFormState(
    textEmbedding: String = "",
): ExtractorTextFieldState {
    return rememberSaveable(saver = ExtractorTextFieldState.Saver) {
        ExtractorTextFieldState(textEmbedding)
    }
}
