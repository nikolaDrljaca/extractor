package com.drbrosdev.extractor.ui.components.showcase

import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.stateIn

class ShowcaseComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val getMostRecentExtraction: suspend () -> ExtractionData?
) {
    private val tickerFlow = ticker(
        delayMillis = ExtractorShowcaseDefaults.SHOWCASE_SAMPLE_RATE.toLong(),
        initialDelayMillis = 0L
    )
        .consumeAsFlow()

    val state = trackExtractionProgress.invoke()
        .combine(tickerFlow) { it, _ ->
            when (it) {
                is ExtractionStatus.Done -> ShowcaseState.Done

                is ExtractionStatus.Running -> {
                    getMostRecentExtraction()
                        .toOption()
                        .fold(
                            ifEmpty = { ShowcaseState.Idle },
                            ifSome = { data ->
                                ShowcaseState.SyncInProgress(data)
                            }
                        )
                }
            }
        }
        .stateIn(
            coroutineScope,
            // NOTE: maybe use WhileSubscribed here
            SharingStarted.Lazily,
            ShowcaseState.Idle
        )
}