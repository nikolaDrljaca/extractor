package com.drbrosdev.extractor.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.usecase.ImageSearch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val imageSearch: ImageSearch
) : ViewModel() {
    private val _state = MutableStateFlow(SearchResultScreenState())
    val state = _state.asStateFlow()

    fun performSearch(query: String) {
        if (query == state.value.searchTerm) return
        if (query.isBlank()) return

        viewModelScope.launch {
            delay(500)
            val result = imageSearch.execute(query)
            _state.update {
                SearchResultScreenState(
                    images = result.getOrDefault(emptyList()),
                    searchTerm = query
                )
            }
        }
    }
}

data class SearchResultScreenState(
    val images: List<MediaImage> = emptyList(),
    val searchTerm: String = "Loading..."
)