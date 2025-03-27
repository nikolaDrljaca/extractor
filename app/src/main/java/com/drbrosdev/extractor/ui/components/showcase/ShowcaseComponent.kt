package com.drbrosdev.extractor.ui.components.showcase

import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class ShowcaseComponent(
    private val coroutineScope: CoroutineScope,
    private val trackExtractionProgress: TrackExtractionProgress,
    private val getMostRecentExtraction: suspend () -> ExtractionData?
) {
    private val progress = trackExtractionProgress.invoke()
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            null
        )

    val state = showcaseStateFlow()
        .stateIn(
            coroutineScope,
            // NOTE: Cancel flow 1 second after all collectors are stopped
            SharingStarted.WhileSubscribed(1_000),
            ShowcaseState.Idle
        )

    private fun showcaseStateFlow() = flow {
        while (true) {
            // emit state immediately
            emit(createState(progress.value))
            // check current progress
            when (progress.value) {
                // if done - break the while loop and stop emitting to flow
                is ExtractionStatus.Done -> break

                // if running - delay next emission by sample rate
                is ExtractionStatus.Running ->
                    delay(ExtractorShowcaseDefaults.SHOWCASE_SAMPLE_RATE)

                // otherwise wait and try again
                else -> delay(300)
            }
        }
    }

    private suspend fun createState(status: ExtractionStatus?) = when {
        status == null -> ShowcaseState.Idle

        else -> when (status) {
            is ExtractionStatus.Done -> ShowcaseState.Done

            is ExtractionStatus.Running -> getMostRecentExtraction()
                .toOption()
                .fold(
                    ifEmpty = { ShowcaseState.Idle },
                    ifSome = { ShowcaseState.SyncInProgress(it) }
                )
        }
    }
}