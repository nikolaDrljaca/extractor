package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractordatefilter.isEmpty
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState


class ExtractorSearchSheetState(
    private val eventHandler: (ExtractorSearchSheetEvents) -> Unit,
    private val stateHandle: SavedStateHandle
) {
    val searchViewState = stateHandle.saveable(
        key = "search_view_state",
        saver = ExtractorSearchViewState.Saver
    ) {
        ExtractorSearchViewState()
    }

    val dateFilterState = stateHandle.saveable(
        key = "date_filter_state",
        saver = ExtractorDateFilterState.Saver()
    ) {
        ExtractorDateFilterState()
    }

    fun onSearch() {
        eventHandler(
            ExtractorSearchSheetEvents.OnSearchClick(
                data = ExtractorSearchSheetEvents.SheetData(
                    dateRange = null,
                    query = searchViewState.query,
                    keywordType = searchViewState.keywordType,
                    searchType = searchViewState.searchType
                )
            )
        )
    }

    fun onDateChange() {
        if (dateFilterState.isEmpty()) {
            eventHandler(
                ExtractorSearchSheetEvents.OnDateChange(
                    data = ExtractorSearchSheetEvents.SheetData(
                        dateRange = null,
                        query = searchViewState.query,
                        keywordType = searchViewState.keywordType,
                        searchType = searchViewState.searchType
                    )
                )
            )
            return
        }
        dateFilterState.dateRange()?.let {
            eventHandler(
                ExtractorSearchSheetEvents.OnDateChange(
                    data = ExtractorSearchSheetEvents.SheetData(
                        dateRange = it,
                        query = searchViewState.query,
                        keywordType = searchViewState.keywordType,
                        searchType = searchViewState.searchType
                    )
                )
            )
        }
    }

    fun onChange() {
        eventHandler(
            ExtractorSearchSheetEvents.OnChange(
                data = ExtractorSearchSheetEvents.SheetData(
                    dateRange = dateFilterState.dateRange(),
                    query = searchViewState.query,
                    keywordType = searchViewState.keywordType,
                    searchType = searchViewState.searchType
                )
            )
        )
    }

    fun enable() {
        searchViewState.enable()
        dateFilterState.enable()
    }

    fun disable() {
        searchViewState.disable()
        dateFilterState.disable()
    }
}
