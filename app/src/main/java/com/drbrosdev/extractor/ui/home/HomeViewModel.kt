package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.PreviousSearchDao
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val previousSearchDao: PreviousSearchDao,
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao,
    private val workManager: WorkManager
) : ViewModel() {

    private val percentageDoneFlow = flow {
        while (true) {
            //TODO Will need more logic once we have a coroutine based extraction on demand
            val isWorking = workManager.getWorkInfosForUniqueWork(WorkNames.EXTRACTOR_WORK).await()
            if (isWorking.isEmpty()) {
                emit(null)
                break
            }

            if (isWorking.first().state.isFinished) {
                emit(null)
                break
            }

            val onDevice = mediaImageRepository.getCount()
            val inStorage = extractionEntityDao.getCount()
            println("OnDevice $onDevice and inStorage = $inStorage")
            val percentage = (inStorage / onDevice).times(100)
            emit(percentage)
            delay(3000L)
        }
    }

    private val prevSearchesFlow = previousSearchDao
        .findAll()
        .map { searches ->
            searches.reversed().take(7)
        }

    val state = combine(percentageDoneFlow, prevSearchesFlow) { percentageDone, prevSearches ->
        HomeUiState(
            searches = prevSearches,
            donePercentage = percentageDone
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState())

    fun deletePreviousSearch(value: PreviousSearchEntity) {
        viewModelScope.launch {
            previousSearchDao.delete(value)
        }
    }
}
