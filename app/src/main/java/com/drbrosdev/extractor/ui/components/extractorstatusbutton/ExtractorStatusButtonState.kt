package com.drbrosdev.extractor.ui.components.extractorstatusbutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class ExtractorStatusButtonState(
    initialStatus: Status = Status.Idle
) {
    var status by mutableStateOf<Status>(initialStatus)
        private set

    fun update(status: Status) {
        this.status = status
    }

    sealed interface Status {

        data object Idle : Status

        data class Working(val donePercentage: Int) : Status
    }

    companion object {
        fun Saver() = androidx.compose.runtime.saveable.Saver<ExtractorStatusButtonState, Int>(
            save = {
                when (val status = it.status) {
                    Status.Idle -> -1
                    is Status.Working -> status.donePercentage
                }
            },
            restore = {
                val state = when (it) {
                    -1 -> Status.Working(it)
                    else -> Status.Idle
                }
                ExtractorStatusButtonState(state)
            }
        )
    }
}

@Composable
fun rememberExtractorStatusButtonState(): ExtractorStatusButtonState {
    return rememberSaveable(saver = ExtractorStatusButtonState.Saver()) {
        ExtractorStatusButtonState()
    }
}

