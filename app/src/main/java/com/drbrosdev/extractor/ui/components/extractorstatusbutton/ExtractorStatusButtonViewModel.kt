package com.drbrosdev.extractor.ui.components.extractorstatusbutton

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ExtractorStatusButtonViewModel(
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao,
    private val workManager: WorkManager
) : ViewModel() {
    val state = percentageDoneFlow()
        .map {
            when {
                it != null -> ExtractorStatusButtonState.Working(it)
                else -> ExtractorStatusButtonState.Idle
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ExtractorStatusButtonState.Idle)

    private fun percentageDoneFlow(): Flow<Int?> = flow {
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

            val onDevice = mediaImageRepository.getCount().toDouble()
            val inStorage = extractionEntityDao.getCount().toDouble()
            val percentage = inStorage.div(onDevice).times(100).toInt()
            emit(percentage)
            delay(1500L)
        }
    }
}

sealed interface ExtractorStatusButtonState {

    data object Idle: ExtractorStatusButtonState

    data class Working(val donePercentage: Int) : ExtractorStatusButtonState
}
