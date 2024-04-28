package com.drbrosdev.extractor.ui.components.extractorstatusbutton



sealed interface ExtractorStatusButtonState {

    data object Idle : ExtractorStatusButtonState

    data class ExtractionRunning(val donePercentage: Int) : ExtractorStatusButtonState

    data object OutOfSync : ExtractorStatusButtonState
}

