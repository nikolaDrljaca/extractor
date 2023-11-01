package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorTextField(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    },
    onChange: (String) -> Unit,
    onDoneSubmit: () -> Unit = {},
) {

    val textStyle = MaterialTheme.typography.headlineMedium
    val interactionSource = remember {
        MutableInteractionSource()
    }

    BasicTextField(
        value = text,
        onValueChange = {
            onChange(it)
        },
        modifier = Modifier
            .then(modifier),
        minLines = 1,
        maxLines = 2,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneSubmit() }
        ),
        textStyle = textStyle.copy(color = Color.White),
        cursorBrush = SolidColor(Color.White),
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = it,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(0.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                placeholder = {
                    Text(
                        text = "Search here...",
                        style = textStyle,
                        color = textColor.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search Images",
                        tint = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp).clickable { onDoneSubmit() }
                    )
                }
            )
        }
    )
}
