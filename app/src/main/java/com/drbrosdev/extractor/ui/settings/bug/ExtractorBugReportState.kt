package com.drbrosdev.extractor.ui.settings.bug

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.setValue

@Stable
class ExtractorBugReportState(
    initUserText: String = "",
    initIncludeEventLogs: Boolean = false
) {

    var userText by mutableStateOf(initUserText)
        private set

    var includeEventLogs by mutableStateOf(initIncludeEventLogs)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var enabled by mutableStateOf(true)
        private set

    fun onUserTextChanged(newValue: String) {
        userText = newValue
    }

    fun onIncludeEventLogsChanged(newValue: Boolean) {
        includeEventLogs = newValue
    }

    fun onIsLoadingChanged(newValue: Boolean) {
        isLoading = newValue
    }

    fun disable() {
        enabled = false
    }

    fun enable() {
        enabled = true
    }

    companion object {
        val Saver = object : Saver<ExtractorBugReportState, Map<String, Any>> {
            override fun restore(value: Map<String, Any>): ExtractorBugReportState {
                return ExtractorBugReportState(
                    initUserText = value.getOrDefault("userText", "") as? String ?: "",
                    initIncludeEventLogs = value.getOrDefault("includeEventLogs", false) as? Boolean
                        ?: false
                )
            }

            override fun SaverScope.save(value: ExtractorBugReportState): Map<String, Any> {
                return mapOf(
                    "userText" to value.userText,
                    "includeEventLogs" to value.includeEventLogs
                )
            }
        }
    }
}
