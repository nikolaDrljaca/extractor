package com.drbrosdev.extractor.ui.imageinfo.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R

@Composable
fun TextAnnotationEditor(
    modifier: Modifier = Modifier,
    text: TextFieldState
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.text_embeddings),
            style = MaterialTheme.typography.headlineSmall
        )
        MinimalTextField(
            state = text
        )
    }
}

@Composable
private fun MinimalTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    placeholder: String? = null
) {
    BasicTextField(
        state = state,
        textStyle = textStyle,
        modifier = modifier,
        cursorBrush = SolidColor(cursorColor),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(0.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (state.text.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = textStyle.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            )
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}
