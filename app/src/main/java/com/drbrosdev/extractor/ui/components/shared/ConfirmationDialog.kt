package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onAction: (ConfirmationDialogActions) -> Unit,
    colors: ConfirmationDialogColors = ConfirmationDialogDefaults.colors(),
    icon: @Composable () -> Unit = { ConfirmationDialogDefaults.Icon() },
    title: @Composable () -> Unit = { ConfirmationDialogDefaults.Title() },
    text: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.then(modifier),
        icon = icon,
        title = title,
        text = text,
        onDismissRequest = { onAction(ConfirmationDialogActions.Dismiss) },
        confirmButton = {
            TextButton(
                onClick = { onAction(ConfirmationDialogActions.Confirm) }
            ) {
                Text(text = stringResource(R.string.confirm_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onAction(ConfirmationDialogActions.Deny) }
            ) {
                Text(text = stringResource(R.string.confirm_dialog_deny))
            }
        },
        iconContentColor = colors.iconContentColor,
        textContentColor = colors.textContentColor,
        titleContentColor = colors.titleContentColor,
        containerColor = colors.containerColor
    )
}

sealed interface ConfirmationDialogActions {

    data object Deny : ConfirmationDialogActions

    data object Confirm : ConfirmationDialogActions

    data object Dismiss : ConfirmationDialogActions
}

data class ConfirmationDialogColors(
    val containerColor: Color,
    val iconContentColor: Color,
    val titleContentColor: Color,
    val textContentColor: Color
)

object ConfirmationDialogDefaults {

    @Composable
    fun colors(
        containerColor: Color = AlertDialogDefaults.containerColor,
        iconContentColor: Color = AlertDialogDefaults.iconContentColor,
        titleContentColor: Color = AlertDialogDefaults.titleContentColor,
        textContentColor: Color = AlertDialogDefaults.textContentColor
    ): ConfirmationDialogColors {
        return ConfirmationDialogColors(
            containerColor = containerColor,
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor
        )
    }

    @Composable
    fun Icon() {
        androidx.compose.material3.Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "",
            modifier = Modifier.size(28.dp)
        )
    }

    @Composable
    fun Title() {
        Text(text = stringResource(R.string.confirm_dialog_sure))
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ConfirmationDialog(onAction = {}) {
            Text(text = "This is example text.")
        }
    }
}