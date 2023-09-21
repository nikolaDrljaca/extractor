package com.drbrosdev.extractor.ui.components.extractorsearchview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChipData
import com.drbrosdev.extractor.ui.components.datafilterchip.toLabelType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ExtractorSearchViewModel : ViewModel() {
    private var query by mutableStateOf("")
    private var filter by mutableStateOf(LabelType.ALL)

    private val _events = Channel<NavToResults>()
    val events = _events.receiveAsFlow()

    fun onQueryChanged(value: String) {
        query = value
    }

    fun onFilterChanged(value: ImageLabelFilterChipData) {
        filter = value.toLabelType()
    }

    fun performSearch() {
        viewModelScope.launch {
            _events.send(NavToResults(query, filter))
        }
    }

    data class NavToResults(val query: String, val filter: LabelType)
}
