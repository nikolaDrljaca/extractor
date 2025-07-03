package com.drbrosdev.extractor.domain.usecase.album

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository

object NonExistingAlbumId

class CleanupAlbum(
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val albumRepository: AlbumRepository
) {
    suspend fun execute(albumId: Long): Either<NonExistingAlbumId, Unit> =
        either {
            ensure(albumId != 0L) { NonExistingAlbumId }
            val album = ensureNotNull(albumRepository.findAlbumById(albumId)) {
                NonExistingAlbumId
            }
            // identify deleted mediaImage ids
            val deletedMediaIds = album.entries
                .mapNotNull {
                    when {
                        mediaStoreImageRepository.findByUri(it.uri.toUri()) == null -> it.id.id
                        else -> null
                    }
                }
                .toList()
            // delete album entries with these mediaImage ids
            albumRepository.deleteAlbumEntries(deletedMediaIds)
        }
}
