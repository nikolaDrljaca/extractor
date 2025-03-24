package com.drbrosdev.extractor.ui.components.showcase

import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class ShowcaseComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val getMostRecentExtraction: suspend () -> ExtractionData?
) {
    // NOTE: Try a tickerFlow with combine -> progressFlow.combine(ticker) {...}
    val state = trackExtractionProgress.invoke()
        .map {
            when(it) {
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
        .sample(ExtractorShowcaseDefaults.SHOWCASE_SAMPLE_RATE.milliseconds)
        .stateIn(
            coroutineScope,
            // NOTE: maybe use WhileSubscribed here
            SharingStarted.Lazily,
            ShowcaseState.Idle
        )
}