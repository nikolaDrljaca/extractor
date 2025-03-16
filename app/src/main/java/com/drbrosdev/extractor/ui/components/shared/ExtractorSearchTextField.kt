package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorSearchTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onDoneSubmit: () -> Unit,
    focusRequester: FocusRequester = remember {
        FocusRequester()
    },
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true
) {
    val textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp)

    val placeholderText = when {
        enabled -> stringResource(id = R.string.search_here)
        else -> stringResource(R.string.disabled)
    }

    BasicTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .padding(vertical = 4.dp)
            .then(modifier),
        state = textFieldState,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Search
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        onKeyboardAction = { onDoneSubmit() },
        enabled = enabled,
        readOnly = false,
        textStyle = textStyle.copy(color = textColor),
        cursorBrush = SolidColor(textColor),
        decorator = { innerTextField ->
            innerTextField()
            if (textFieldState.text.isBlank()) {
                Text(
                    text = placeholderText,
                    style = textStyle,
                    color = textColor.copy(alpha = 0.5f)
                )
            }
        }
    )
}

@Composable
fun ExtractorSearchPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light)

    val text = when {
        enabled -> stringResource(R.string.search_here)
        else -> stringResource(R.string.disabled)
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .then(modifier),
        enabled = enabled,
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
                text = text,
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
            Column {
                ExtractorSearchPill(
                    onClick = {},
                    modifier = Modifier.padding(8.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExtractorSearchTextField(
                    textFieldState = TextFieldState(),
                    onDoneSubmit = {}
                )
            }
        }
    }
}