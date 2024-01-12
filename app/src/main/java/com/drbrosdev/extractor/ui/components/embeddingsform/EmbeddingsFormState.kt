package com.drbrosdev.extractor.ui.components.embeddingsform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class EmbeddingsFormState(
    initTextEmbedding: String,
    initUserEmbedding: String,
) {
    var textEmbedding by mutableStateOf(initTextEmbedding)
        private set

    var userEmbedding by mutableStateOf(initUserEmbedding)
        private set

    fun setTextValue(value: String) {
        textEmbedding = value
    }

    fun setUserValue(value: String) {
        userEmbedding = value
    }

    companion object {
        val Saver = run {
            val textKey = "text_key_embed"
            val userKey = "user_key_embed"
            mapSaver(
                save = { mapOf(textKey to it.textEmbedding, userKey to it.userEmbedding) },
                restore = {
                    EmbeddingsFormState(
                        initTextEmbedding = it[textKey] as String,
                        initUserEmbedding = it[userKey] as String
                    )
                }
            )
        }
    }
}


@Composable
fun rememberEmbeddingsFormState(
    textEmbedding: String = "",
    userEmbedding: String = ""
): EmbeddingsFormState {
    return rememberSaveable(saver = EmbeddingsFormState.Saver) {
        EmbeddingsFormState(textEmbedding, userEmbedding)
    }
}
