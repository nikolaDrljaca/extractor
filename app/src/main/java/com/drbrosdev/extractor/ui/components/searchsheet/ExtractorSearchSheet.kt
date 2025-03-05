package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

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
            ExtractorSearchView(
                state = component.searchViewState,
                contentPadding = PaddingValues(),
                onDone = component::onSearch,
                onKeywordTypeChange = component::onChange,
                onSearchTypeChange = component::onChange
            )

            ExtractorDateFilter(
                modifier = Modifier,
                state = component.dateFilterState,
                onDateChanged = component::onDateChange
            )

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
                eventHandler = {},
                stateHandle = SavedStateHandle()
            )
        )
    }
}
