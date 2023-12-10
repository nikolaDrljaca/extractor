package com.drbrosdev.extractor.ui.dialog.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.ExtractionProgress
import com.drbrosdev.extractor.domain.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorStatusDialogViewModel(
    private val bulkExtractor: BulkExtractor,
    private val extractionProgress: ExtractionProgress
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private var extractionJob: Job? = null

    val state = extractionProgress().map {
        when (it) {
            is ExtractionStatus.Done -> ExtractorStatusDialogUiModel(
                onDeviceCount = it.onDeviceCount,
                inStorageCount = it.inStorageCount,
                isExtractionRunning = isLoading.value
            )
            is ExtractionStatus.Running -> ExtractorStatusDialogUiModel(
                onDeviceCount = it.onDeviceCount,
                inStorageCount = it.inStorageCount,
                isExtractionRunning = true
            )
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
