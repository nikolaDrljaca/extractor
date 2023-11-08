package com.drbrosdev.extractor.ui.search

import android.net.Uri
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.LabelType
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
    private val imageSearch: ImageSearchByLabel
) : ViewModel() {
    val searchViewState = ExtractorSearchViewState(
        initialQuery = query,
        initialLabelType = labelType
    )

    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.Success(
            images = emptyList()
        )
    )
    val state = _state.asStateFlow()

    private val labelTypeUpdateFlow = snapshotFlow { searchViewState.labelType }
        .onEach { performSearch() }
        .launchIn(viewModelScope)

    fun performSearch() {
        if (searchViewState.query.isBlank()) return
        viewModelScope.launch {
            _state.update { ExtractorSearchScreenUiState.Loading }
            delay(100)
            val result = imageSearch.search(searchViewState.query, searchViewState.labelType)
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
}

