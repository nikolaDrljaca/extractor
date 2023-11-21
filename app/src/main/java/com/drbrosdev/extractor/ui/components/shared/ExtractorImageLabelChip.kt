package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
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

@Composable
fun ExtractorImageLabelChip(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    text: String,
    isChecked: Boolean,
) {
    InputChip(
        modifier = modifier,
        selected = isChecked,
        onClick = {
            onDismiss()
        },
        label = { Text(text) },
        trailingIcon = {
            trailingIcon?.invoke()
        },
        border = InputChipDefaults.inputChipBorder(
            borderColor = Color.Transparent,
            selectedBorderColor = Color.White,
            selected = isChecked,
            enabled = true
        ),
        shape = CircleShape,
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            selectedContainerColor = Color.Transparent,
        )
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorImageLabelChip(
                text = "Sample",
                isChecked = false,
                onDismiss = {},
                trailingIcon = {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Localized description",
                        Modifier.size(InputChipDefaults.AvatarSize)
                    )
                }
            )

            ExtractorImageLabelChip(
                text = "Sample",
                isChecked = true,
                onDismiss = {}
            )
        }
    }
}