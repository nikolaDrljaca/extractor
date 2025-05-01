package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.ui.dialog.status.safeDiv

sealed class ExtractionProgress {
    abstract val onDeviceCount: Int
    abstract val inStorageCount: Int

    data class Done(
        override val onDeviceCount: Int,
        override val inStorageCount: Int
    ) : ExtractionProgress()

    data class Running(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
    ) : ExtractionProgress() {
        private val percentageCount = inStorageCount safeDiv onDeviceCount
        val percentage = (percentageCount * 100).toInt()
    }
}

fun ExtractionProgress.isDataIncomplete() = onDeviceCount != inStorageCount