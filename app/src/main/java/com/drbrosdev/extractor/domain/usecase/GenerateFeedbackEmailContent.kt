package com.drbrosdev.extractor.domain.usecase

import android.os.Build
import com.drbrosdev.extractor.framework.logger.EventEntity
import com.drbrosdev.extractor.framework.logger.EventLogDao
import com.drbrosdev.extractor.framework.requiresApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GenerateFeedbackEmailContent(
    private val dispatcher: CoroutineDispatcher,
    private val eventLogDao: EventLogDao
) {

    suspend fun execute(
        userContent: String,
        includeEventLogs: Boolean = false
    ) = withContext(dispatcher) {
        if (!includeEventLogs) {
            return@withContext userContent
        }

        val events = eventLogDao.findAll()
            .joinToString(separator = "\n") { parseEvent(it) }

        buildString {
            appendUserContent(userContent)
            appendDeviceInfo()
            appendEventContent(events)
        }
    }

    private fun parseEvent(event: EventEntity) = "${event.timestamp}|${event.tag}|${event.message}"

    private fun StringBuilder.appendDeviceInfo() {
        val runtime = Runtime.getRuntime()
        val maxMemory = when (val max = runtime.maxMemory()) {
            Long.MAX_VALUE -> "No Limit"
            else -> max.toString()
        }
        appendLine("--- Device Info ---")
        appendLine("VM Processors: ${runtime.availableProcessors()}")
        appendLine("VM Memory: $maxMemory")
        // cpu
        requiresApi(apiLevel = 31) {
            appendLine("SoC: ${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}")
        }
    }

    private fun StringBuilder.appendEventContent(eventText: String) {
        if (eventText.isBlank()) {
            append("No event logs stored.")
            return
        }
        appendLine("--- Event Logs ---")
        append(eventText)
    }

    private fun StringBuilder.appendUserContent(text: String) {
        append("\n")
        append(text)
        append("\n\n\n")
    }
}