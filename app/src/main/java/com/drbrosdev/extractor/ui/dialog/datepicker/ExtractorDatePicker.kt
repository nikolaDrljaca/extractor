package com.drbrosdev.extractor.ui.dialog.datepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorDatePicker(
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState()
) {

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            DialogButton(onClick = { onConfirm(state.selectedDateMillis) }) {
                Text(text = stringResource(R.string.datepicker_select))
            }
        },
        dismissButton = {
            DialogButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.datepicker_close))
            }
        },
        colors = DatePickerDefaults.colors()
    ) {
        DatePicker(
            state = state,
            showModeToggle = false
        )
    }
}

@Composable
private fun DialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        content(this)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorDatePicker(onDismiss = { /*TODO*/ }, onConfirm = {})
        }
    }
}
