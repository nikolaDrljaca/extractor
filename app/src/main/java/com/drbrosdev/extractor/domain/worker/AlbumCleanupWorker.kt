package com.drbrosdev.extractor.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class AlbumCleanupWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val albumRepository: AlbumRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val albumId = inputData.getLong(WorkerDataKeys.ALBUM_ID, 0L)
        if (albumId == 0L) return Result.failure()

        val album = albumRepository.findAlbumById(albumId) ?: return Result.failure()

        val deletedMediaIds = withContext(Dispatchers.Default) {
            album.entries.asFlow()
                .map {
                    val uri = it.uri.toUri()
                    val mediaImage = mediaStoreImageRepository.findByUri(uri)

                    when {
                        mediaImage == null -> it // not found, delete it
                        else -> null
                    }
                }
                .filterNotNull()
                .map { it.id.id }
                .toList()
        }

        albumRepository.deleteAlbumEntries(deletedMediaIds)
        return Result.success()
    }
}