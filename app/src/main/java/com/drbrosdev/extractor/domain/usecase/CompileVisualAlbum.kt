package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CompileVisualAlbum(
    private val dispatcher: CoroutineDispatcher,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        val mostUsed = visualEmbeddingDao.getMostUsed(7).first()

        mostUsed.forEach { embedUsage ->
            val embeds = imageEmbeddingsDao.findByVisualEmbedding(embedUsage.value)

            val entries = embeds.map {
                NewAlbum.Entry(
                    uri = MediaImageUri(it.imageEntity.uri),
                    id = MediaImageId(it.imageEntity.mediaStoreId)
                )
            }

            val newAlbum = NewAlbum(
                keyword = embedUsage.value,
                name = embedUsage.value,
                searchType = SearchType.FULL,
                labelType = LabelType.IMAGE,
                entries = entries,
                origin = NewAlbum.Origin.VISUAL_COMPUTED
            )

            albumRepository.createAlbum(newAlbum)
        }
    }
}