package com.drbrosdev.extractor.ui.dialog.datepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.searchsheet.isRangeSelected
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorDatePicker(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    state: DateRangePickerState = rememberDateRangePickerState()
) {
    val isConfirmEnabled by remember {
        derivedStateOf {
            state.isRangeSelected()
        }
    }
    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            DialogButton(
                enabled = isConfirmEnabled,
                onClick = onConfirm
            ) {
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
        DateRangePicker(
            state = state,
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        )
    }
}

@Composable
private fun DialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.then(modifier),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = enabled
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
            ExtractorDatePicker(onDismiss = {  }, onConfirm = {})
        }
    }
}
