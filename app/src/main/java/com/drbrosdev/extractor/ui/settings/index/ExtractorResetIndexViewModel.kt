package com.drbrosdev.extractor.ui.settings.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorResetIndexViewModel(
    private val extractorRepository: ExtractorRepository,
    private val spawnExtractorWork: SpawnExtractorWork
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val actionLoading = _loading.asStateFlow()

    private val _events = Channel<ExtractorResetIndexEvents>()
    val events = _events.receiveAsFlow()

    fun resetImageIndex() {
        viewModelScope.launch {
            _loading.update { true }

            extractorRepository.deleteExtractionDataAndSearchIndex()
            spawnExtractorWork.invoke()

            _events.send(ExtractorResetIndexEvents.WorkerStarted)
            _loading.update { false }
        }
    }
}

sealed interface ExtractorResetIndexEvents {
    data object WorkerStarted : ExtractorResetIndexEvents
}