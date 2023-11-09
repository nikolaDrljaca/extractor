package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.LabelType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ExtractorSearchViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val state = ExtractorSearchViewState(
        initialQuery = savedStateHandle.get<String>("query") ?: "",
        LabelType.ALL
    )

    private val _events = Channel<NavToResults>()
    val events = _events.receiveAsFlow()

    private val saveJob = snapshotFlow { state.query }
        .debounce(500L)
        .onEach { savedStateHandle["query"] = it }
        .launchIn(viewModelScope)

    fun performSearch() {
        if (state.query.isBlank()) return
        viewModelScope.launch {
            _events.send(NavToResults(state.query, state.labelType))
        }
    }

    data class NavToResults(val query: String, val filter: LabelType)
}
