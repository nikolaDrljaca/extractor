package com.drbrosdev.extractor.ui.dialog.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.ExtractionProgress
import com.drbrosdev.extractor.domain.model.isDataIncomplete
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExtractorStatusDialogViewModel(
    private val workerService: ExtractorWorkerService,
    private val trackExtractionProgress: TrackExtractionProgress
) : ViewModel() {

    val state = trackExtractionProgress.invoke()
        .map {
            when (it) {
                is ExtractionProgress.Done -> {
                    when {
                        it.isDataIncomplete() -> ExtractorStatusDialogUiState.CanStart(
                            inStorageCount = it.inStorageCount,
                            onDeviceCount = it.onDeviceCount,
                            eventSink = { startExtraction() }
                        )

                        else -> ExtractorStatusDialogUiState.Done(
                            inStorageCount = it.inStorageCount,
                            onDeviceCount = it.onDeviceCount,
                            eventSink = { }
                        )
                    }
                }

                is ExtractionProgress.Running -> ExtractorStatusDialogUiState.InProgress(
                    inStorageCount = it.inStorageCount,
                    onDeviceCount = it.onDeviceCount,
                    eventSink = { }
                )
            }
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorStatusDialogUiState.InProgress(
                onDeviceCount = 0,
                inStorageCount = 0,
                eventSink = { }
            )
        )

    private fun startExtraction() {
        workerService.startExtractorWorker()
    }
}
