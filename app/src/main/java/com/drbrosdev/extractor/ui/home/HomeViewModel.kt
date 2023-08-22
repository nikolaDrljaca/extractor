package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ImageDataDao
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.BulkExtractor
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
    private val imageSearch: ImageSearch,
    private val bulkExtractor: BulkExtractor
) : ViewModel() {

    private val syncStatus = flow {
        while (true) {
            val localCount = imageDataDao.getCount()
            val deviceCount = mediaImageRepository.getCount()
            emit(SyncStatus(localCount, deviceCount))
            delay(5000L)
        }
    }

    private val imagesFlow = MutableStateFlow<List<MediaImage>>(emptyList())
    private val loadingFlow = MutableStateFlow(false)

    val state = combine(syncStatus, imagesFlow, loadingFlow) { sync, images, loading ->
        HomeUiState(sync, images, loading)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState())

    fun consumeEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.PerformSearch -> performSearch(event.query)
            HomeScreenEvents.RunExtraction -> viewModelScope.launch {
                bulkExtractor.execute()
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            loadingFlow.update { true }
            val result = imageSearch.execute(query)
            imagesFlow.update { result }
            loadingFlow.update { false }
        }
    }
}
