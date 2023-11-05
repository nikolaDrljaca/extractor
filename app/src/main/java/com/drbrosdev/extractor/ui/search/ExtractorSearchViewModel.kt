package com.drbrosdev.extractor.ui.search

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

class ExtractorSearchViewModel(
    private val query: String,
    private val labelType: LabelType,
    private val imageSearch: ImageSearchByLabel
) : ViewModel() {
    private val _state = MutableStateFlow<ExtractorSearchScreenUiState>(
        ExtractorSearchScreenUiState.Success(
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
            _state.update { ExtractorSearchScreenUiState.Loading(_labelType.value, searchTerm = query) }
            delay(100)
            val result = imageSearch.search(query, _labelType.value)
            _state.update {
                ExtractorSearchScreenUiState.Success(
                    images = result,
                    searchTerm = query,
                    labelType = _labelType.value
                )
            }
        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorSearchScreenUiState.Loading -> emptyList()
            is ExtractorSearchScreenUiState.Success -> out.images.map { it.uri }
        }
    }

    fun setLabelType(labelType: LabelType) = _labelType.update { labelType }
}

