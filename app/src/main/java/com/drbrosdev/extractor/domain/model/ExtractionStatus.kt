package com.drbrosdev.extractor.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

enum class ExtractionStatus {
    DONE, RUNNING
}

fun Flow<ExtractionProgress>.asStatus() =
    map {
        when (it) {
            is ExtractionProgress.Done -> ExtractionStatus.DONE
            is ExtractionProgress.Running -> ExtractionStatus.RUNNING
        }
    }
        .distinctUntilChanged()