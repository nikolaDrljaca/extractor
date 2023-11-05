package com.drbrosdev.extractor.ui.components.embeddingsform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.shared.EmbeddingTextField
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageLabelChip
import com.drbrosdev.extractor.ui.imageinfo.VisualEmbedUiModel
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmbeddingsForm(
    onClearVisual: (String) -> Unit,
    modifier: Modifier = Modifier,
    formState: EmbeddingsFormState = rememberEmbeddingsFormState(),
    visualEmbeddings: List<VisualEmbedUiModel>,
) {
    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(text = "Visual Embeddings", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            visualEmbeddings.forEach {
                ExtractorImageLabelChip(
                    isChecked = it.isChecked,
                    text = it.text,
                    onDismiss = { onClearVisual(it.text) },
                    trailingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Localized description",
                            Modifier.size(InputChipDefaults.AvatarSize)
                        )
                    }
                )
            }
        }

        EmbeddingTextField(
            value = formState.textEmbedding,
            onTextChange = formState::setTextValue,
            label = { Text(text = "Text Embeddings") }
        )

        EmbeddingTextField(
            value = formState.userEmbedding,
            onTextChange = formState::setUserValue,
            label = { Text(text = "User Embeddings") },
            placeholder = { Text(text = "Type your search terms here...") }
        )
    }
}

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
        fun create(text: String, user: String) =
            EmbeddingsFormState(text, user)
    }
}


@Composable
fun rememberEmbeddingsFormState(
    key: Any? = null,
    textEmbedding: String = "",
    userEmbedding: String = ""
): EmbeddingsFormState {
    return remember(key1 = key) {
        EmbeddingsFormState.create(textEmbedding, userEmbedding)
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        EmbeddingsForm(
            onClearVisual = {},
            visualEmbeddings = emptyList(),
            formState = rememberEmbeddingsFormState(
                textEmbedding = "",
                userEmbedding = ""
            )
        )
    }
}