package com.drbrosdev.extractor.ui.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorStatusDialogViewModel(
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao,
    private val workManager: WorkManager
) : ViewModel() {
    val state = flow {
        while (true) {
            emit(updateState())
            delay(5000L)
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ExtractorStatusDialogUiModel()
        )

    fun startExtractionSync() {
        val extractorWorkRequest = OneTimeWorkRequestBuilder<ExtractorWorker>()
            .build()

        workManager.enqueueUniqueWork(
            WorkNames.EXTRACTOR_WORK,
            ExistingWorkPolicy.KEEP,
            extractorWorkRequest
        )

        viewModelScope.launch { updateState() }
    }

    private suspend fun updateState(): ExtractorStatusDialogUiModel {
        val onDevice = mediaImageRepository.getCount()
        val inStorage = extractionEntityDao.getCount()
        val workInfo = workManager.getWorkInfosForUniqueWork(WorkNames.EXTRACTOR_WORK).await()

        val isWorking = when {
            workInfo.isNotEmpty() -> !workInfo.first().state.isFinished
            else -> false
        }

        return ExtractorStatusDialogUiModel(
            onDeviceCount = onDevice,
            inStorageCount = inStorage,
            isExtractionRunning = isWorking
        )
    }
}


data class ExtractorStatusDialogUiModel(
    val onDeviceCount: Int = 0,
    val inStorageCount: Int = 0,
    val isExtractionRunning: Boolean = false
) {
    val percentage = inStorageCount safeDiv onDeviceCount
}

infix fun Int.safeDiv(other: Int): Float {
    if (other == 0) return 0f
    return this.toDouble().div(other.toDouble()).toFloat()
}
