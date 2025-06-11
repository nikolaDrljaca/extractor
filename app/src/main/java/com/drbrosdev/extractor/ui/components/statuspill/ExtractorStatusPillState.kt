package com.drbrosdev.extractor.ui.components.statuspill

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ExtractorStatusPillState {
    // sync is idle -- show leftover search count
    @Immutable
    data class Idle(
        val searchesLeft: Int
    ) : ExtractorStatusPillState

    // search count is disabled - show how many images are indexed
    @Immutable
    data class Disabled(
        val indexCount: Int
    ): ExtractorStatusPillState

    // extraction is running -- report progress
    @Immutable
    data class SyncInProgress(
        val progress: Int
    ) : ExtractorStatusPillState

    // auto sync is off -- report extraction out of sync and numbers
    data object OutOfSync : ExtractorStatusPillState
}