package com.drbrosdev.extractor.ui.components.previoussearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.domain.model.PreviousSearch
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
        .findAll()
        .map { searches ->
            searches.map { it.toPreviousSearch() }
                .reversed()
                .take(7)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deletePreviousSearch(value: PreviousSearch) {
        viewModelScope.launch {
            previousSearchDao.delete(value.toEntity())
        }
    }
}

