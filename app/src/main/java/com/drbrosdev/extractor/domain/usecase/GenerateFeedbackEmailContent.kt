package com.drbrosdev.extractor.domain.usecase

import android.os.Build
import com.drbrosdev.extractor.BuildConfig
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

    private fun StringBuilder.appendUserContent(text: String) {
        if (text.isNotBlank()) {
            append(text)
            append("\n\n")
        }
    }

    private fun StringBuilder.appendDeviceInfo() {
        val runtime = Runtime.getRuntime()
        val maxMemory = when (val max = runtime.maxMemory()) {
            Long.MAX_VALUE -> "No Limit"
            else -> max.toString()
        }
        appendLine("--- Device Info ---")
        appendLine("Device: ${Build.MANUFACTURER} ${Build.PRODUCT}")
        appendLine("VM Processors: ${runtime.availableProcessors()}")
        appendLine("VM Memory: $maxMemory")
        // cpu
        requiresApi(apiLevel = 31) {
            appendLine("SoC: ${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}")
        }
        appendLine("SDK Version: ${Build.VERSION.SDK_INT}")
        appendLine("Lupa Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        append("\n")
    }

    private fun StringBuilder.appendEventContent(eventText: String) {
        if (eventText.isBlank()) {
            append("No event logs stored.")
            return
        }
        appendLine("--- Event Logs ---")
        append(eventText)
    }

    private fun parseEvent(event: EventEntity) = "${event.timestamp}|${event.tag}|${event.message}"
}