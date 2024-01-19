package com.drbrosdev.extractor.domain.usecase

import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImageRepository
import com.drbrosdev.extractor.ui.dialog.status.safeDiv
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ExtractionProgress(
    private val dispatcher: CoroutineDispatcher,
    private val extractionDao: ExtractionDao,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val workManager: WorkManager
) {

    operator fun invoke(): Flow<ExtractionStatus> =
        combine(
            workManager.getWorkInfosForUniqueWorkLiveData(WorkNames.EXTRACTOR_WORK).asFlow(),
            extractionDao.getCountAsFlow(),
            mediaStoreImageRepository.getCountAsFlow()
        ) { isWorking, inStorage, onDevice ->
            when {
                isWorking.isEmpty() -> ExtractionStatus.Done(onDevice, inStorage)
                isWorking.first().state.isFinished -> ExtractionStatus.Done(onDevice, inStorage)
                else -> ExtractionStatus.Running(onDevice, inStorage)
            }
        }
            .flowOn(dispatcher)
}

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