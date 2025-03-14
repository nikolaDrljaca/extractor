package com.drbrosdev.extractor.ui.dialog.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.worker.ExtractorWorkerService
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorStatusDialogViewModel(
    private val workerService: ExtractorWorkerService,
    private val trackExtractionProgress: TrackExtractionProgress
) : ViewModel() {

    private val _events = Channel<ExtractorStatusDialogEvents>()
    val events = _events.receiveAsFlow()

    val state = trackExtractionProgress.invoke()
        .map {
            when (it) {
                is ExtractionStatus.Done -> {
                    when {
                        it.isDataIncomplete -> ExtractorStatusDialogUiState.CanStart(
                            inStorageCount = it.inStorageCount,
                            onDeviceCount = it.onDeviceCount,
                            eventSink = { startExtraction() }
                        )

                        else -> ExtractorStatusDialogUiState.Done(
                            inStorageCount = it.inStorageCount,
                            onDeviceCount = it.onDeviceCount,
                            eventSink = { closeDialog() }
                        )
                    }
                }

                is ExtractionStatus.Running -> ExtractorStatusDialogUiState.InProgress(
                    inStorageCount = it.inStorageCount,
                    onDeviceCount = it.onDeviceCount,
                    eventSink = { closeDialog() }
                )
            }
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorStatusDialogUiState.InProgress(
                onDeviceCount = 0,
                inStorageCount = 0,
                eventSink = { closeDialog() }
            )
        )

    private fun startExtraction() {
        workerService.startExtractorWorker()
    }

    private fun closeDialog() {
        viewModelScope.launch {
            _events.send(ExtractorStatusDialogEvents.CloseDialog)
        }
    }
}
