package com.drbrosdev.extractor.ui.settings.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorResetIndexViewModel(
    private val extractorRepository: ExtractorRepository,
    private val extractionProgress: TrackExtractionProgress,
    private val spawnExtractorWork: SpawnExtractorWork
) : ViewModel() {

    val loading = extractionProgress.invoke()
        .map {
            it is ExtractionStatus.Running
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

            extractorRepository.deleteExtractionDataAndSearchIndex()
            spawnExtractorWork.invoke()

            _events.send(ExtractorResetIndexEvents.WorkerStarted)
        }
    }
}

sealed interface ExtractorResetIndexEvents {
    data object WorkerStarted : ExtractorResetIndexEvents
}