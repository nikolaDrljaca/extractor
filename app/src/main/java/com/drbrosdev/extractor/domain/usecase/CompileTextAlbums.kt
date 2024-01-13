package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class CompileTextAlbums(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val imageEmbeddingsDao: ImageEmbeddingsDao,
    private val albumRepository: AlbumRepository,
    private val tokenizeText: TokenizeText,
    private val validateToken: ValidateToken
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        val allText = textEmbeddingDao.findAllTextEmbedValues()

        val tokens = tokenizeText(allText)
            .filter { validateToken(it) }
            .map { it.text }
            .toList()

        val job = tokens
            .createFrequencyMap()
            .generateMostCommon()
            .asFlow()
            .map { topWord ->
                val strategy = ImageEmbeddingSearchStrategy.Partial(topWord)
                val embeddings = imageEmbeddingsDao.findByTextEmbeddingFts(strategy.query)
                embeddings to topWord
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .map {
                val newAlbum = it.createNewAlbumFromPayload()
                albumRepository.createAlbum(newAlbum)
            }
            .flowOn(dispatcher)
            .launchIn(this)

    }

    private fun List<String>.createFrequencyMap(): Map<String, Int> {
        return groupingBy { it }.eachCount()
    }

    private fun Map<String, Int>.generateMostCommon(amount: Int = 7): List<String> {
        return this.entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(amount)
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
            keywordType = KeywordType.TEXT,
            entries = entries,
            origin = NewAlbum.Origin.TEXT_COMPUTED
        )
    }
}