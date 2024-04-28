package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberExtractorSearchBottomSheetState(): SheetState {
    return rememberStandardBottomSheetState(
        confirmValueChange = {
            when (it) {
                SheetValue.Hidden -> false
                SheetValue.Expanded -> true
                SheetValue.PartiallyExpanded -> true
            }
        }
    )
}