package com.drbrosdev.extractor.ui.settings.bug

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.drbrosdev.extractor.domain.usecase.GenerateFeedbackEmailContent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ExtractorBugReportViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val generateEmailContent: GenerateFeedbackEmailContent
) : ViewModel() {

    val state = savedStateHandle.saveable(
        key = "bug_report_state",
        saver = ExtractorBugReportState.Saver
    ) {
        ExtractorBugReportState()
    }

    private val _events = Channel<ExtractorBugReportEvents>()
    val events = _events.receiveAsFlow()

    fun onSubmitFeedback() {
        state.disable()
        state.onIsLoadingChanged(true)
        viewModelScope.launch {
            val out = generateEmailContent.execute(
                userContent = state.userText,
                includeEventLogs = state.includeEventLogs
            )

            _events.send(ExtractorBugReportEvents.SendEmail(out))

            state.enable()
            state.onIsLoadingChanged(false)
        }
    }

}

sealed interface ExtractorBugReportEvents {
    data class SendEmail(val content: String) : ExtractorBugReportEvents
}
