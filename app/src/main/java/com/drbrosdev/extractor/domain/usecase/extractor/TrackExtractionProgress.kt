package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class TrackExtractionProgress(
    private val dispatcher: CoroutineDispatcher,
    private val repo: ExtractorRepository,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val workerService: ExtractorWorkerService,
) {

    operator fun invoke(): Flow<ExtractionStatus> {
        return workerService.workInfoAsFlow(ExtractorWorkerService.EXTRACTOR_WORK)
            .flatMapLatest { isWorking ->
                when {
                    isWorking.isEmpty() -> flowOf(
                        ExtractionStatus.Done(
                            onDeviceCount = repo.getExtractionCountAsFlow().first(),
                            inStorageCount = mediaStoreImageRepository.getCount()
                        )
                    )

                    isWorking.first().state.isFinished -> flowOf(
                        ExtractionStatus.Done(
                            onDeviceCount = repo.getExtractionCountAsFlow().first(),
                            inStorageCount = mediaStoreImageRepository.getCount()
                        )
                    )

                    else -> combine(
                        repo.getExtractionCountAsFlow(),
                        mediaStoreImageRepository.getCountAsFlow()
                    ) { inStorage, onDevice -> ExtractionStatus.Running(onDevice, inStorage) }
                }
            }
            .flowOn(dispatcher)
    }
}