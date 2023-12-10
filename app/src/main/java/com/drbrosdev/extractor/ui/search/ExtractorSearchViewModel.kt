package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.ExtractionProgress
import com.drbrosdev.extractor.domain.ExtractionStatus
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.usecase.image.search.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.image.search.SearchStrategy
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRange
import com.drbrosdev.extractor.ui.components.extractordatefilter.dateRangeAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorsearchview.isBlank
import com.drbrosdev.extractor.ui.components.extractorsearchview.labelTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.queryAsFlow
import com.drbrosdev.extractor.ui.components.extractorsearchview.searchTypeAsFlow
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorSearchViewModel(
    query: String,
    labelType: LabelType,
    private val imageSearch: ImageSearchByLabel,
    private val extractionProgress: ExtractionProgress,
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    val extractorStatusButtonState = ExtractorStatusButtonState()

    val searchViewState = ExtractorSearchViewState(
        initialQuery = stateHandle["query"] ?: query,
        initialLabelType = labelType,
    )

    val dateFilterState = ExtractorDateFilterState()

    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.FirstSearch
    )
    val state = _state.asStateFlow()

    private val lastQuery = MutableStateFlow(LastQuery(query, labelType))

    private val progressJob = extractionProgress()
        .onEach {
            val status = when (it) {
                is ExtractionStatus.Done -> ExtractorStatusButtonState.Status.Idle
                is ExtractionStatus.Running -> ExtractorStatusButtonState.Status.Working(it.percentage)
            }
            extractorStatusButtonState.update(status)
        }
        .launchIn(viewModelScope)

    private val labelTypeUpdateFlow = searchViewState.labelTypeAsFlow()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    private val searchTypeUpdateFlow = searchViewState.searchTypeAsFlow()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    private val saveQueryJob = searchViewState.queryAsFlow()
        .onEach { stateHandle["query"] = it }
        .launchIn(viewModelScope)

    private val dateFilterJob = dateFilterState.dateRangeAsFlow()
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    fun performSearch(searchStrategy: SearchStrategy) {
        when (searchStrategy) {
            SearchStrategy.NORMAL -> runSearch()
            SearchStrategy.DIRTY_CHECKING -> {
                if (searchViewState.isBlank()) return
                if (
                    lastQuery.value.isOldQuery(
                        searchViewState.query,
                        searchViewState.labelType
                    )
                ) return
                runSearch()
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorSearchScreenUiState.Success -> out.images.map { it.uri.uri.toUri() }
            else -> emptyList()
        }
    }

    private fun runSearch() {
        if (searchViewState.isBlank()) return

        _state.update { ExtractorSearchScreenUiState.Loading }

        viewModelScope.launch {
            val searchQuery = ImageSearchByLabel.Params(
                query = searchViewState.query,
                labelType = searchViewState.labelType,
                dateRange = dateFilterState.dateRange(),
                type = searchViewState.searchType
            )

            val result = imageSearch.search(searchQuery).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.labelType) }
            }

            _state.createFrom(result)
        }
    }

    private data class LastQuery(val query: String, val labelType: LabelType)

    private fun LastQuery.isOldQuery(query: String, labelType: LabelType): Boolean {
        return when {
            (query == this.query) and (labelType == this.labelType) -> true
            else -> false
        }
    }
}

