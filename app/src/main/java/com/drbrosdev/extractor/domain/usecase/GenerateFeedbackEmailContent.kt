package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.framework.logger.EventEntity
import com.drbrosdev.extractor.framework.logger.EventLogDao
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
            appendEventContent(events)
        }
    }

    private fun parseEvent(event: EventEntity) = "${event.timestamp}|${event.tag}|${event.message}"

    private fun StringBuilder.appendEventContent(eventText: String) {
        if (eventText.isBlank()) {
            append("No event logs stored.")
            return
        }
        append("--- Event Logs ---\n")
        append(eventText)
    }

    private fun StringBuilder.appendUserContent(text: String) {
        append("\n")
        append(text)
        append("\n\n\n")
    }
}