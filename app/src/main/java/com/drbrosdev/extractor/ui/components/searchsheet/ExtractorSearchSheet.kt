package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun ExtractorSearchSheet(
    modifier: Modifier = Modifier,
    isHidden: Boolean = false,
    state: ExtractorSearchSheetState,
) {
    val alphaOffset by animateFloatAsState(targetValue = if (isHidden) 0f else 1f, label = "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExtractorSearchView(
            state = state.searchViewState,
            isHidden = isHidden,
            contentPadding = PaddingValues(),
            textFieldPadding = PaddingValues(bottom = 16.dp),
            onDone = state::onSearch,
            onKeywordTypeChange = state::onChange,
            onSearchTypeChange = state::onChange
        )

        ExtractorDateFilter(
            modifier = Modifier.graphicsLayer {
                alpha = alphaOffset
            },
            state = state.dateFilterState,
            onDateChanged = state::onDateChange
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchSheet(
                isHidden = false,
                state = ExtractorSearchSheetState(
                    eventHandler = {},
                    stateHandle = SavedStateHandle()
                )
            )
        }
    }
}
