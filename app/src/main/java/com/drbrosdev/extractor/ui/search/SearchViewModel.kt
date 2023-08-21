package com.drbrosdev.extractor.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(
    private val imageDataDao: ImageDataDao,
    private val mediaImageRepository: MediaImageRepository
) : ViewModel() {

    val syncStatus = flow {
        while(true) {
            delay(1000L)
            val localCount = imageDataDao.getCount()
            val deviceCount = mediaImageRepository.getCount()
            emit(SyncStatus(localCount, deviceCount))
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SyncStatus())
}

data class SyncStatus(
    val localCount: Int = 0,
    val deviceCount: Int = 0
)