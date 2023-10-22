package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorImageLabelChip(
    modifier: Modifier = Modifier,
    text: String,
    onDismiss: () -> Unit,
) {
    InputChip(
        modifier = modifier,
        selected = false,
        onClick = {
            onDismiss()
        },
        label = { Text(text) },
        trailingIcon = {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        border = InputChipDefaults.inputChipBorder(
            borderColor = Color.Transparent
        ),
        shape = CircleShape,
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White
        )
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorImageLabelChip(
            text = "Sample",
            onDismiss = {}
        )
    }
}