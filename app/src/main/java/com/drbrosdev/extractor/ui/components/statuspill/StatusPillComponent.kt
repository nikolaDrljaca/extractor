package com.drbrosdev.extractor.ui.components.statuspill

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatusPillComponent(
    private val coroutineScope: CoroutineScope,
    private val trackProgress: TrackExtractionProgress,
    private val dataStore: ExtractorDataStore,
) {
    val state = combine(
        trackProgress(),
        dataStore.searchCount
    ) { progress, count ->
        when (progress) {
            is ExtractionStatus.Done -> {
                when {
                    progress.isDataIncomplete -> ExtractorStatusPillState.OutOfSync
                    else -> ExtractorStatusPillState.Idle(searchesLeft = count)
                }
            }

            is ExtractionStatus.Running -> ExtractorStatusPillState.SyncInProgress(
                progress = progress.percentage
            )
        }
    }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            ExtractorStatusPillState.Idle(0)
        )
}