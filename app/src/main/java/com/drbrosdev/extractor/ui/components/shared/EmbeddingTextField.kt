package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun EmbeddingTextField(
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    initialValue: String = "",
) {
    var text by rememberSaveable {
        mutableStateOf(initialValue)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = text,
        onValueChange = {
            text = it
            onTextChange(it)
        },
        label = label,
        minLines = 4,
        colors = TextFieldDefaults.colors(
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}


@Composable
fun EmbeddingTextField(
    value: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
) {

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = value,
        onValueChange = onTextChange,
        label = label,
        minLines = 4,
        maxLines = 7,
        colors = TextFieldDefaults.colors(
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        EmbeddingTextField(onTextChange = {})
    }
}
