package com.drbrosdev.extractor.domain

import androidx.work.WorkManager
import androidx.work.await
import com.drbrosdev.extractor.data.dao.ExtractionDao
import com.drbrosdev.extractor.domain.worker.WorkNames
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImageRepository
import com.drbrosdev.extractor.ui.dialog.status.safeDiv
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ExtractionProgress(
    private val dispatcher: CoroutineDispatcher,
    private val extractionDao: ExtractionDao,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val workManager: WorkManager
) {

    operator fun invoke(): Flow<ExtractionStatus> = flow {
        while (true) {
            val isWorking = workManager.getWorkInfosForUniqueWork(WorkNames.EXTRACTOR_WORK).await()
            val onDevice = mediaStoreImageRepository.getCount()
            val inStorage = extractionDao.getCount()

            when {
                isWorking.isEmpty() -> {
                    emit(ExtractionStatus.Done(onDevice, inStorage))
                    break
                }

                isWorking.first().state.isFinished -> {
                    emit(ExtractionStatus.Done(onDevice, inStorage))
                    break
                }
            }

            val out = ExtractionStatus.Running(
                onDeviceCount = onDevice,
                inStorageCount = inStorage
            )

            emit(out)
            delay(1000L)
        }
    }.flowOn(dispatcher)
}

sealed class ExtractionStatus {
    abstract val onDeviceCount: Int
    abstract val inStorageCount: Int

    data class Done(
        override val onDeviceCount: Int,
        override val inStorageCount: Int
    ) : ExtractionStatus()

    data class Running(
        override val onDeviceCount: Int,
        override val inStorageCount: Int,
    ) : ExtractionStatus() {
        val percentageCount = inStorageCount safeDiv onDeviceCount
        val percentage = (percentageCount * 100).toInt()
    }
}