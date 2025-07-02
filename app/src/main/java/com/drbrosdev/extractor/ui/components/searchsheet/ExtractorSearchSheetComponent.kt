package com.drbrosdev.extractor.ui.components.searchsheet

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.saveable
import arrow.core.raise.nullable
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.util.EpochMillis
import com.drbrosdev.extractor.util.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class ExtractorSearchSheetComponent(
    private val onSearchEvent: (ImageSearchParams) -> Unit,
    private val stateHandle: SavedStateHandle
) {
    val query = stateHandle.saveable(
        key = "search_view_query",
        saver = TextFieldState.Saver
    ) {
        TextFieldState()
    }
    var keywordType by stateHandle.saveable {
        mutableStateOf(KeywordType.ALL)
    }
    var searchType by stateHandle.saveable {
        mutableStateOf(SearchType.PARTIAL)
    }

    val dateRangePickerState = stateHandle.saveable(
        key = "search_view_date_range",
        saver = listSaver(
            save = { listOf(it.selectedStartDateMillis, it.selectedEndDateMillis) },
            restore = {
                DateRangePickerState(
                    locale = Locale.getDefault(),
                    initialSelectedStartDateMillis = it.getOrNull(0),
                    initialSelectedEndDateMillis = it.getOrNull(1),
                )
            }
        )
    ) {
        DateRangePickerState(Locale.getDefault())
    }
    var shouldShowDateRangePicker by mutableStateOf(false)

    val focusRequester = FocusRequester()

    fun onSearch() = onSearchEvent(getSearchParamSnapshot())

    fun requestFocus() = focusRequester.requestFocus()

    fun showDateRangePicker() {
        shouldShowDateRangePicker = true
    }

    fun onResetDateSelection() {
        dateRangePickerState.setSelection(null, null)
        onSearchEvent(getSearchParamSnapshot())
    }

    fun hideDateRangePicker() {
        shouldShowDateRangePicker = false
    }

    fun onKeywordTypeChange(value: KeywordType) {
        keywordType = value
        onSearchEvent(getSearchParamSnapshot())
    }

    fun onSearchTypeChange(value: SearchType) {
        searchType = value
        onSearchEvent(getSearchParamSnapshot())
    }

    fun onDateRangeConfirm() {
        shouldShowDateRangePicker = false
        onSearchEvent(getSearchParamSnapshot())
    }

    fun clearState() {
        query.setTextAndPlaceCursorAtEnd("")
        keywordType = KeywordType.ALL
        searchType = SearchType.FULL
        dateRangePickerState.setSelection(null, null)
    }

    fun getSearchParamSnapshot() = ImageSearchParams(
        dateRange = dateRangePickerState.dateRange(),
        query = query.text.toString(),
        keywordType = keywordType,
        searchType = searchType
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun DateRangePickerState.isRangeSelected(): Boolean {
    return (selectedStartDateMillis != null) and (selectedEndDateMillis != null)
}

@OptIn(ExperimentalMaterial3Api::class)
fun DateRangePickerState.dateRange() = nullable {
    val startRange = EpochMillis(selectedStartDateMillis.bind())
    val endRange = EpochMillis(selectedEndDateMillis.bind())
    DateRange(
        startRange.toLocalDateTime(),
        endRange.toLocalDateTime()
    )
}
