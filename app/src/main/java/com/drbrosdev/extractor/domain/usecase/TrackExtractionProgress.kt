package com.drbrosdev.extractor.domain.usecase

import androidx.lifecycle.asFlow
import androidx.work.WorkManager
import com.drbrosdev.extractor.data.extraction.dao.ExtractionDao
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.worker.WorkNames
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class TrackExtractionProgress(
    private val dispatcher: CoroutineDispatcher,
    private val repo: ExtractorRepository,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val workManager: WorkManager
) {
    operator fun invoke(): Flow<ExtractionStatus> =
        combine(
            workManager.getWorkInfosForUniqueWorkLiveData(WorkNames.EXTRACTOR_WORK).asFlow(),
            repo.getExtractionCountAsFlow(),
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
