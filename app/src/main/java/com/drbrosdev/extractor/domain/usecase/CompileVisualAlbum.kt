package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CompileVisualAlbum(
    private val dispatcher: CoroutineDispatcher,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val searchImageByKeyword: SearchImageByKeyword,
    private val albumRepository: AlbumRepository
) {

    suspend operator fun invoke() {
        visualEmbeddingDao.getMostUsed(7)
            .asFlow()
            .map { embedUsage ->
                val params = SearchImageByKeyword.Params(
                    query = embedUsage.value,
                    keywordType = KeywordType.IMAGE,
                    type = SearchType.PARTIAL,
                    dateRange = null
                )
                val result = searchImageByKeyword.execute(params)
                result to embedUsage.value
            }
            // do not create albums with no entries, therefore filter out empty embeddings list
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .flowOn(dispatcher)
            .collect {
                val newAlbum = it.createNewAlbumFromPayload()
                albumRepository.createAlbum(newAlbum)
            }
    }

    private fun CompileAlbumPayload.createNewAlbumFromPayload(): NewAlbum {
        val (embeds, embedUsage) = this
        val entries = embeds.map {
            NewAlbum.Entry(
                uri = it.uri,
                id = it.mediaImageId
            )
        }

        return NewAlbum(
            keyword = embedUsage,
            name = embedUsage,
            searchType = SearchType.FULL,
            keywordType = KeywordType.IMAGE,
            entries = entries,
            origin = NewAlbum.Origin.VISUAL_COMPUTED
        )
    }
}

typealias CompileAlbumPayload = Pair<List<Extraction>, String>