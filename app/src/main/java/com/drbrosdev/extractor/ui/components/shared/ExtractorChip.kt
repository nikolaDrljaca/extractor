package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorChip(
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
            enabled = false
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

@Composable
fun ExtractorActionChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    colors: SelectableChipColors = ExtractorActionChipDefaults.actionChipColors(),
    content: @Composable () -> Unit,
) {
    Card {

    }
    InputChip(
        modifier = modifier,
        selected = false,
        onClick = onClick,
        label = { content() },
        leadingIcon = {
            leadingIcon?.invoke()
        },
        border = BorderStroke(0.dp, Color.Transparent),
        shape = CircleShape,
        colors = colors
    )
}

object ExtractorActionChipDefaults {

    private val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )

    private val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    @Composable
    fun actionChipColors(
        contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
    ): SelectableChipColors {
        return InputChipDefaults.inputChipColors(
            labelColor = contentColor,
            leadingIconColor = leadingIconColor,
            containerColor = containerColor,
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

            ExtractorActionChip(
                onClick = { /*TODO*/ },
                leadingIcon = { Icon(Icons.Rounded.Build, contentDescription = null) }
            ) {
                Text("Action")
            }
        }
    }
}