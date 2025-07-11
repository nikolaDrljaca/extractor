package com.drbrosdev.extractor.ui.settings.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.ExtractionProgress
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorResetIndexViewModel(
    private val lupaImageRepository: LupaImageRepository,
    private val extractionProgress: TrackExtractionProgress,
    private val workerService: ExtractorWorkerService
) : ViewModel() {

    val loading = extractionProgress.invoke()
        .map {
            it is ExtractionProgress.Running
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            false
        )

    private val _events = Channel<ExtractorResetIndexEvents>()
    val events = _events.receiveAsFlow()

    fun resetImageIndex() {
        // if extraction is running, return
        if (loading.value) return

        viewModelScope.launch {
            lupaImageRepository.deleteExtractionDataAndSearchIndex()

            workerService.startExtractorWorker()

            _events.send(ExtractorResetIndexEvents.WorkerStarted)
        }
    }
}

sealed interface ExtractorResetIndexEvents {
    data object WorkerStarted : ExtractorResetIndexEvents
}