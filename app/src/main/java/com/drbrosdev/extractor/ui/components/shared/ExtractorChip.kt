package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorChip(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: SelectableChipColors = ExtractorChipDefaults.chipColors(),
    text: String,
    isChecked: Boolean,
) {
    InputChip(
        modifier = modifier
            .animateContentSize(),
        selected = isChecked,
        onClick = {
            onDismiss()
        },
        label = { Text(text) },
        leadingIcon = {
            leadingIcon?.invoke()
        },
        trailingIcon = {
            if (!isChecked) {
                trailingIcon?.invoke()
            }
        },
        border = InputChipDefaults.inputChipBorder(
            borderColor = Color.Transparent,
            selectedBorderColor = Color.White,
            selected = isChecked,
            enabled = !isChecked
        ),
        shape = CircleShape,
        colors = colors
    )
}

@Composable
fun ExtractorChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: SelectableChipColors = ExtractorChipDefaults.surfaceChipColors(),
    text: String,
) {
    InputChip(
        modifier = modifier,
        selected = false,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = {
            leadingIcon?.invoke()
        },
        trailingIcon = {
            trailingIcon?.invoke()
        },
        border = InputChipDefaults.inputChipBorder(
            borderColor = Color.Transparent,
            selectedBorderColor = Color.White,
            enabled = false,
            selected = true
        ),
        shape = CircleShape,
        colors = colors
    )
}

object ExtractorChipDefaults {

    private val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )

    private val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    @Composable
    fun surfaceChipColors(
        contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
    ): SelectableChipColors {
        return InputChipDefaults.inputChipColors(
            labelColor = contentColor,
            leadingIconColor = iconColor,
            containerColor = containerColor,
            trailingIconColor = iconColor
        )
    }

    @Composable
    fun chipColors(
        contentColor: Color = MaterialTheme.colorScheme.onPrimary,
        containerColor: Color = MaterialTheme.colorScheme.primary,
        iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    ): SelectableChipColors {
        return InputChipDefaults.inputChipColors(
            labelColor = contentColor,
            leadingIconColor = iconColor,
            containerColor = containerColor,
            trailingIconColor = iconColor,
            selectedContainerColor = Color.Transparent
        )
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorChip(
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

            ExtractorChip(
                text = "Sample",
                isChecked = true,
                onDismiss = {}
            )

            ExtractorChip(
                text = "Sample",
                onClick = {},
                colors = ExtractorChipDefaults.surfaceChipColors()
            )
        }
    }
}