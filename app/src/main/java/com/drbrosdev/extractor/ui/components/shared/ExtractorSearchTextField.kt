package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Deprecated("")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorSearchTextField(
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
    },
    enabled: Boolean = true
) {
    val textStyle = MaterialTheme.typography.titleLarge

    val placeholderText = when {
        enabled -> stringResource(id = R.string.search_here)
        else -> stringResource(R.string.disabled)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            interactionSource = interactionSource,
            modifier = Modifier
                .weight(1f),
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
            enabled = enabled,
            minLines = 1,
            maxLines = 2,
            readOnly = false,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            decorationBox = {
                TextFieldDefaults.DecorationBox(
                    value = text,
                    innerTextField = it,
                    enabled = enabled,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(),
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
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledTextColor = Color.Gray,
                    ),
                    placeholder = {
                        Text(
                            text = placeholderText,
                            style = textStyle,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }
                )
            }
        )

        Surface(
            color = textColor.copy(alpha = 0.2f),
            onClick = onDoneSubmit,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp),
            enabled = enabled
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search Images",
                tint = textColor.copy(alpha = 0.5f),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ExtractorSearchPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Search,
                "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(id = R.string.search_here),
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchPill(
                onClick = {},
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}