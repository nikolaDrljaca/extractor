package com.drbrosdev.extractor.ui.result

import android.net.Uri
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
    private val _state = MutableStateFlow<SearchResultScreenState>(SearchResultScreenState.Loading)
    val state = _state.asStateFlow()

    fun performSearch(query: String) {
        if (isQuerySame(query)) return

        viewModelScope.launch {
            delay(100)
            val result = imageSearch.execute(query)
            _state.update {
                SearchResultScreenState.Success(
                    images = result.getOrDefault(emptyList()),
                    searchTerm = query
                )
            }

        }
    }

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            SearchResultScreenState.Loading -> emptyList()
            is SearchResultScreenState.Success -> out.images.map { it.uri }
        }
    }

    private fun isQuerySame(query: String): Boolean {
        return when (val out = state.value) {
            is SearchResultScreenState.Success -> (out.searchTerm == query) or query.isBlank()
            is SearchResultScreenState.Loading -> false
        }
    }
}

sealed interface SearchResultScreenState {

    data class Success(
        val images: List<MediaImage> = emptyList(),
        val searchTerm: String = "Loading..."
    ) : SearchResultScreenState

    data object Loading : SearchResultScreenState

}
