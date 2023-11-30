package com.drbrosdev.extractor.ui.components.previoussearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.domain.model.toEntity
import com.drbrosdev.extractor.domain.model.toPreviousSearch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreviousSearchesViewModel(
    private val previousSearchDao: PreviousSearchDao
) : ViewModel() {
    val prevSearchesFlow = previousSearchDao
        .findAllAndTakeFlow(10)
        .map { searches ->
            searches.map { it.toPreviousSearch().toItemState() }
                .reversed()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deletePreviousSearch(value: PreviousSearchItemState) {
        viewModelScope.launch {
            previousSearchDao.delete(value.toPreviousSearch().toEntity())
        }
    }
}

