package com.drbrosdev.extractor.ui.components.extractordatefilter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.nullable
import com.drbrosdev.extractor.domain.model.DateRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Stable
class ExtractorDateFilterState(
    initStart: Option<Long> = None,
    initEnd: Option<Long> = None,
) {
    var startDate by mutableStateOf(initStart)
        private set

    var endDate by mutableStateOf(initEnd)
        private set

    var selection by mutableStateOf(ExtractorDateFilterSelection.NONE)
        private set

    var disabled by mutableStateOf(false)
        private set

    fun showStartSelection() {
        selection = ExtractorDateFilterSelection.START
    }

    fun showEndSelection() {
        selection = ExtractorDateFilterSelection.END
    }

    fun updateStartDate(new: Long?) {
        startDate = if (new == null) {
            None
        } else {
            Some(new)
        }
        clearSelection()
    }

    fun updateEndDate(new: Long?) {
        endDate = if (new == null) {
            None
        } else {
            Some(new)
        }
        clearSelection()
    }

    fun clearSelection() {
        selection = ExtractorDateFilterSelection.NONE
    }

    fun clearDates() {
        startDate = None
        endDate = None
    }

    fun enable() {
        if (!disabled) return
        disabled = false
    }

    fun disable() {
        if (disabled) return
        disabled = true
    }

    companion object {
        fun Saver() = listSaver(
            save = {
                val start = it.startDate.getOrNull()
                val end = it.endDate.getOrNull()
                listOf(start, end)
            },
            restore = {
                ExtractorDateFilterState(
                    initStart = Option.fromNullable(it[0]),
                    initEnd = Option.fromNullable(it[1])
                )
            }
        )
    }
}

fun ExtractorDateFilterState.startDateAsFlow(): Flow<Option<LocalDateTime>> {
    return snapshotFlow { startDate }
        .map {
            it.map { value ->
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(value),
                    ZoneId.systemDefault()
                )
            }
        }
}

fun ExtractorDateFilterState.endDateAsFlow(): Flow<Option<LocalDateTime>> {
    return snapshotFlow { endDate }
        .map {
            it.map { value ->
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(value),
                    ZoneId.systemDefault()
                )
            }
        }
}

fun ExtractorDateFilterState.dateRange(): DateRange? {
    return nullable {
        val start = startDate.bind()
        val end = endDate.bind()
        DateRange(
            start = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault()),
            end = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault())
        )
    }
}

fun ExtractorDateFilterState.isEmpty(): Boolean {
    return startDate.isNone() && endDate.isNone()
}

fun ExtractorDateFilterState.dateRangeAsFlow(): Flow<DateRange?> {
    return combine(
        snapshotFlow { startDate },
        snapshotFlow { endDate }
    ) { start, end ->
        nullable {
            val _start = start.bind()
            val _end = end.bind()
            val _startDate =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(_start), ZoneId.systemDefault())
            val _endDate =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(_end), ZoneId.systemDefault())
            DateRange(_startDate, _endDate)
        }
    }
}

@Composable
fun rememberExtractorDateFilterState(
    initStart: Long? = null,
    initEnd: Long? = null
): ExtractorDateFilterState {
    return rememberSaveable(saver = ExtractorDateFilterState.Saver()) {
        ExtractorDateFilterState(
            initStart = Option.fromNullable(initStart),
            initEnd = Option.fromNullable(initEnd)
        )
    }
}

enum class ExtractorDateFilterSelection {
    START, END, NONE
}

fun Option<Long>.getAsString(default: String): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return when (this) {
        None -> default
        is Some -> LocalDateTime
            .ofInstant(Instant.ofEpochMilli(this.value), ZoneId.systemDefault())
            .format(formatter)
    }
}
