package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ExtractionProgress
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class TrackExtractionProgress(
    private val dispatcher: CoroutineDispatcher,
    private val repo: LupaImageRepository,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val workerService: ExtractorWorkerService,
) {

    operator fun invoke(): Flow<ExtractionProgress> {
        return combine(
            repo.getCountAsFlow(),
            mediaStoreImageRepository.getCountAsFlow(),
            workerService.workInfoAsFlow(ExtractorWorkerService.EXTRACTOR_WORK)
        ) { inStorage, onDevice, workStatus ->
            when {
                // no job is queued
                workStatus == null -> ExtractionProgress.Done(
                    inStorageCount = inStorage,
                    onDeviceCount = onDevice
                )

                // queued jobs are finished
                workStatus.state.isFinished -> ExtractionProgress.Done(
                    inStorageCount = inStorage,
                    onDeviceCount = onDevice
                )

                else -> ExtractionProgress.Running(
                    inStorageCount = inStorage,
                    onDeviceCount = onDevice
                )
            }
        }
            .distinctUntilChanged()
            .flowOn(dispatcher)
    }
}