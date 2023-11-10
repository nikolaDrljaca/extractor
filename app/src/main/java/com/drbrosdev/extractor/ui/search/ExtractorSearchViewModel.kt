package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.usecase.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.SearchStrategy
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import kotlinx.coroutines.delay
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
    private val stateHandle: SavedStateHandle,
) : ViewModel() {
    val searchViewState = ExtractorSearchViewState(
        initialQuery = stateHandle["query"] ?: query,
        initialLabelType = labelType
    )

    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.Success(
            images = emptyList()
        )
    )
    val state = _state.asStateFlow()

    private val lastQuery = MutableStateFlow(LastQuery(query, labelType))

    private val labelTypeUpdateFlow = snapshotFlow { searchViewState.labelType }
        .onEach { performSearch(SearchStrategy.NORMAL) }
        .launchIn(viewModelScope)

    private val saveQueryJob = snapshotFlow { searchViewState.query }
        .onEach { stateHandle["query"] = it }
        .launchIn(viewModelScope)

    fun performSearch(searchStrategy: SearchStrategy) {
        when (searchStrategy) {
            SearchStrategy.NORMAL -> runSearch()
            SearchStrategy.DIRTY_CHECKING -> {
                if (searchViewState.query.isBlank()) return
                if (lastQuery.value.isOldQuery(
                        searchViewState.query,
                        searchViewState.labelType
                    )
                ) return
                runSearch()
            }
        }
    }

    private fun runSearch() {
        viewModelScope.launch {
            _state.update { ExtractorSearchScreenUiState.Loading }
            delay(100)
            val result = imageSearch.search(searchViewState.query, searchViewState.labelType).also {
                lastQuery.update { LastQuery(searchViewState.query, searchViewState.labelType) }
            }
            _state.update {
                ExtractorSearchScreenUiState.Success(images = result)
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorSearchScreenUiState.Loading -> emptyList()
            is ExtractorSearchScreenUiState.Success -> out.images.map { it.uri }
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

