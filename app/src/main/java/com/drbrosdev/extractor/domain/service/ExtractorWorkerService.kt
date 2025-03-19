package com.drbrosdev.extractor.domain.service

interface ExtractorWorkerService {

    fun startExtractorWorker()

    fun startAlbumCleanupWorker(albumId: Long)

}