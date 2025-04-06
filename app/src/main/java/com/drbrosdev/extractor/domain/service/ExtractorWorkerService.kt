package com.drbrosdev.extractor.domain.service

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface ExtractorWorkerService {

    fun startExtractorWorker()

    fun startAlbumCleanupWorker(albumId: Long)

    fun workInfoAsFlow(workName: String): Flow<List<WorkInfo>>

    companion object {
        const val EXTRACTOR_WORK = "extractor_work"
        const val EXTRACTOR_PERIODIC = "periodic_extractor_work"
        const val ALBUM_CLEANUP = "album_cleanup_work"

        const val DATA_ALBUM_ID = "ALBUM_ID"
    }
}