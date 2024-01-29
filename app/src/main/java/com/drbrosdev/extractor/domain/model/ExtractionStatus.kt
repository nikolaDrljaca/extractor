package com.drbrosdev.extractor.domain.model

import com.drbrosdev.extractor.ui.dialog.status.safeDiv

sealed class ExtractionStatus {
    abstract val onDeviceCount: Int
    abstract val inStorageCount: Int

    data class Done(
        override val onDeviceCount: Int,
        override val inStorageCount: Int
    ) : ExtractionStatus() {
        val isDataIncomplete = onDeviceCount != inStorageCount
    }

    data class Running(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
    ) : ExtractionStatus() {
        val percentageCount = inStorageCount safeDiv onDeviceCount
        val percentage = (percentageCount * 100).toInt()
    }
}
