package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.ImageSearch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val imageDataDao: ImageDataDao,
    private val mediaImageRepository: MediaImageRepository,
    private val imageSearch: ImageSearch
) : ViewModel() {

    private val syncStatus = flow {
        while (true) {
            delay(1000L)
            val localCount = imageDataDao.getCount()
            val deviceCount = mediaImageRepository.getCount()
            emit(SyncStatus(localCount, deviceCount))
        }
    }

    private val imagesFlow = MutableStateFlow<List<MediaImage>>(emptyList())

    val state = combine(syncStatus, imagesFlow) { sync, images ->
        HomeScreenState(sync, images)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeScreenState())

    fun consumeEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.PerformSearch -> performSearch(event.query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            val result = imageSearch.execute(query)
            imagesFlow.update { result }
        }
    }
}

data class SyncStatus(
    val localCount: Int = 0,
    val deviceCount: Int = 0
)

data class HomeScreenState(
    val syncStatus: SyncStatus = SyncStatus(),
    val images: List<MediaImage> = emptyList()
)

sealed interface HomeScreenEvents {
    data class PerformSearch(val query: String) : HomeScreenEvents

}