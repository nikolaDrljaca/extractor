package com.drbrosdev.extractor.ui.result

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.LabelType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val query: String,
    private val labelType: LabelType,
    private val imageSearch: ImageSearchByLabel
) : ViewModel() {
    private val _state = MutableStateFlow<SearchResultUiState>(
        SearchResultUiState.Success(
            images = emptyList(),
            searchTerm = query,
            labelType = labelType
        )
    )
    val state = _state.asStateFlow()

    private val _labelType = MutableStateFlow(labelType)
    private val labelTypeUpdateFlow = _labelType
        .onEach { performSearch() }
        .launchIn(viewModelScope)

    private fun performSearch() {
        viewModelScope.launch {
            _state.update { SearchResultUiState.Loading(_labelType.value, searchTerm = query) }
            delay(100)
            val result = imageSearch.search(query, _labelType.value)
            _state.update {
                SearchResultUiState.Success(
                    images = result,
                    searchTerm = query,
                    labelType = _labelType.value
                )
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is SearchResultUiState.Loading -> emptyList()
            is SearchResultUiState.Success -> out.images.map { it.uri }
        }
    }

    fun setLabelType(labelType: LabelType) = _labelType.update { labelType }
}

