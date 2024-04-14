package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExtractorStatusDialogViewModel(
    private val spawnExtractorWork: SpawnExtractorWork,
    private val trackExtractionProgress: TrackExtractionProgress
) : ViewModel() {

    val state = trackExtractionProgress.invoke().map {
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
        spawnExtractorWork.invoke()
    }
}


@Immutable
data class ExtractorStatusDialogUiModel(
    val onDeviceCount: Int = 0,
    val inStorageCount: Int = 0,
    val isExtractionRunning: Boolean = false
) {
    val percentage = inStorageCount safeDiv onDeviceCount

    val percentageText = "${percentage.times(100).toInt()}%"

    val shouldAllowExtraction = when {
        isExtractionRunning -> false
        onDeviceCount == inStorageCount -> false
        else -> true
    }
}

infix fun Int.safeDiv(other: Int): Float {
    if (other == 0) return 0f
    return this.toDouble().div(other.toDouble()).toFloat()
}
