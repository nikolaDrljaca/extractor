package com.drbrosdev.extractor.domain.worker

interface ExtractorWorkerService {

    fun startExtractorWorker()

    fun startAlbumCleanupWorker(albumId: Long)

}