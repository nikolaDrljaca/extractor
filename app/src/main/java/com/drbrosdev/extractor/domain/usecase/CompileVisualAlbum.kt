package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class CompileVisualAlbum(
    private val dispatcher: CoroutineDispatcher,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        visualEmbeddingDao.getMostUsed(7)
            .asFlow()
            .map { embedUsage ->
                val embeddings = imageEmbeddingsDao.findByVisualEmbedding(embedUsage.value)
                embeddings to embedUsage.value
            }
            // do not create albums with no entries, therefore filter out empty embeddings list
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .map { it.createNewAlbumFromPayload() }
            .onEach { newAlbum -> albumRepository.createAlbum(newAlbum) }
            .launchIn(this)
    }

    private fun CompileAlbumPayload.createNewAlbumFromPayload(): NewAlbum {
        val (embeds, embedUsage) = this
        val entries = embeds.map {
            NewAlbum.Entry(
                uri = MediaImageUri(it.imageEntity.uri),
                id = MediaImageId(it.imageEntity.mediaStoreId)
            )
        }

        return NewAlbum(
            keyword = embedUsage,
            name = embedUsage,
            searchType = SearchType.FULL,
            labelType = LabelType.IMAGE,
            entries = entries,
            origin = NewAlbum.Origin.VISUAL_COMPUTED
        )
    }
}

typealias CompileAlbumPayload = Pair<List<ImageEmbeddingsRelation>, String>