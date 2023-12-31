package com.drbrosdev.extractor.ui.dialog.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.ExtractionProgress
import com.drbrosdev.extractor.domain.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExtractorStatusDialogViewModel(
    private val spawnExtractorWork: SpawnExtractorWork,
    private val extractionProgress: ExtractionProgress
) : ViewModel() {

    val state = extractionProgress().map {
        when (it) {
            is ExtractionStatus.Done -> ExtractorStatusDialogUiModel(
                onDeviceCount = it.onDeviceCount,
                inStorageCount = it.inStorageCount,
                isExtractionRunning = false
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

    fun startExtraction() {
        spawnExtractorWork()
    }
}


data class ExtractorStatusDialogUiModel(
    val onDeviceCount: Int = 0,
    val inStorageCount: Int = 0,
    val isExtractionRunning: Boolean = false
) {
    val percentage = inStorageCount safeDiv onDeviceCount
    val shouldAllowExtraction = !isExtractionRunning or (onDeviceCount != inStorageCount)
}

infix fun Int.safeDiv(other: Int): Float {
    if (other == 0) return 0f
    return this.toDouble().div(other.toDouble()).toFloat()
}
