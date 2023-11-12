package com.drbrosdev.extractor.ui.dialog.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorStatusDialogViewModel(
    private val mediaImageRepository: MediaImageRepository,
    private val extractionEntityDao: ExtractionEntityDao,
    private val bulkExtractor: BulkExtractor,
    private val workManager: WorkManager
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private var extractionJob: Job? = null

    val state = flow {
        while (true) {
            emit(updateState())
            delay(2000L)
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ExtractorStatusDialogUiModel()
        )

    fun startExtractionSync() {
        if (extractionJob == null) {
            extractionJob = viewModelScope.launch {
                isLoading.update { true }
                bulkExtractor.execute()
            }
            extractionJob!!.invokeOnCompletion {
                isLoading.update { false }
            }
        }
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
            isExtractionRunning = isWorking or isLoading.value
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
