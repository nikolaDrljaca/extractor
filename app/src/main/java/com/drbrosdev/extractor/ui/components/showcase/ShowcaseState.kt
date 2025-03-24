package com.drbrosdev.extractor.ui.components.showcase

import com.drbrosdev.extractor.domain.model.ExtractionData

sealed interface ShowcaseState {
    data object Done : ShowcaseState

    data object Idle : ShowcaseState

    data class SyncInProgress(
        val mostRecentExtraction: ExtractionData
    ) : ShowcaseState
}