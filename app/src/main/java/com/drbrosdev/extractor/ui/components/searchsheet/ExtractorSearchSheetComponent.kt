package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractordatefilter.isEmpty
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState

@Stable
class ExtractorSearchSheetComponent(
    private val eventHandler: (ExtractorSearchSheetEvent) -> Unit,
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
            ExtractorSearchSheetEvent.OnSearch(
                params = ImageSearchParams(
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
                ExtractorSearchSheetEvent.OnDateChange(
                    params = ImageSearchParams(
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
                ExtractorSearchSheetEvent.OnDateChange(
                    params = ImageSearchParams(
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
            ExtractorSearchSheetEvent.OnChange(
                params = ImageSearchParams(
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
