package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.KeywordTypeChips
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toChipDataIndex
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.toKeywordType
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchTextField
import com.drbrosdev.extractor.ui.components.shared.SearchTypeSwitch
import com.drbrosdev.extractor.ui.dialog.datepicker.ExtractorDatePicker
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorSearchSheet(
    modifier: Modifier = Modifier,
    component: ExtractorSearchSheetComponent,
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExtractorSearchTextField(
                textFieldState = component.query,
                onDoneSubmit = component::onSearch,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .focusRequester(component.focusRequester)
                    .fillMaxWidth(),
            )

            KeywordTypeChips(
                onFilterChanged = {
                    val keywordType = it.toKeywordType()
                    component.onKeywordTypeChange(keywordType)
                },
                selection = component.keywordType.toChipDataIndex(),
            )

            SearchTypeSwitch(
                selection = component.searchType,
                onSelectionChanged = component::onSearchTypeChange,
            )

            ExtractorDateFilter(
                modifier = Modifier,
                onClick = component::showDateRangePicker,
                onReset = { component.dateRangePickerState.setSelection(null, null) },
                state = component.dateRangePickerState
            )

            if (component.shouldShowDateRangePicker) {
                ExtractorDatePicker(
                    onDismiss = component::hideDateRangePicker,
                    onConfirm = component::onDateRangeConfirm, //
                    modifier = Modifier,
                    state = component.dateRangePickerState
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = true) {
        ExtractorSearchSheet(
            component = ExtractorSearchSheetComponent(
                onSearchEvent = {},
                stateHandle = SavedStateHandle()
            )
        )
    }
}
