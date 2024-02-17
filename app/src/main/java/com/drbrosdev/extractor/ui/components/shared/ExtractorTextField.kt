package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorTextField(
    modifier: Modifier = Modifier,
    text: String,
    onChange: (String) -> Unit,
    onDoneSubmit: () -> Unit,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    textColor: Color = when {
        isSystemInDarkTheme() -> Color.White
        else -> Color.Black
    }
) {
    val textStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 26.sp
    )

    TextField(
        interactionSource = interactionSource,
        modifier = Modifier
            .then(modifier),
        value = text,
        onValueChange = onChange,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneSubmit() },
            onSearch = { onDoneSubmit() }
        ),
        minLines = 1,
        maxLines = 2,
        readOnly = false,
        textStyle = textStyle.copy(color = textColor),
        placeholder = {
            Text(
                text = stringResource(R.string.search_here),
                style = textStyle,
                color = textColor.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            Surface(
                color = textColor.copy(alpha = 0.2f),
                onClick = onDoneSubmit,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Images",
                    tint = textColor.copy(alpha = 0.5f),
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = textColor,
            selectionColors = TextSelectionColors(
                backgroundColor = textColor.copy(alpha = 0.4f),
                handleColor = textColor
            ),
            focusedTextColor = MaterialTheme.colorScheme.onPrimary
        ),
    )
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column {
            ExtractorTextField(text = "", onChange = {}, onDoneSubmit = {})
            ExtractorTextField(text = "sample", onChange = {}, onDoneSubmit = {})
        }
    }
}