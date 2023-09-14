package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val previousSearchDao: PreviousSearchDao
) : ViewModel() {

    val state = previousSearchDao
        .findAll()
        .map { searches ->
            HomeUiState(
                searches = searches.reversed().take(7)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState())

    fun deletePreviousSearch(value: PreviousSearchEntity) {
        viewModelScope.launch {
            previousSearchDao.delete(value)
        }
    }
}
