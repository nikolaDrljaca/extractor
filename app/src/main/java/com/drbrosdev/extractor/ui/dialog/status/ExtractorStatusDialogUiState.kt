package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.runtime.Immutable


sealed class ExtractorStatusDialogUiState {
    abstract val onDeviceCount: Int
    abstract val inStorageCount: Int
    abstract val eventSink: () -> Unit

    @Immutable
    data class Done(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
        override val eventSink: () -> Unit,
    ) : ExtractorStatusDialogUiState()

    @Immutable
    data class InProgress(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
        override val eventSink: () -> Unit,
    ) : ExtractorStatusDialogUiState() {
        val percentageText = "${(inStorageCount safeDiv onDeviceCount).times(100).toInt()}%"
    }

    @Immutable
    data class CanStart(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
        override val eventSink: () -> Unit
    ) : ExtractorStatusDialogUiState()
}

sealed interface ExtractorStatusDialogEvents {

    data object CloseDialog : ExtractorStatusDialogEvents
}

infix fun Int.safeDiv(other: Int): Float {
    if (other == 0) return 0f
    return this.toDouble().div(other.toDouble()).toFloat()
}
