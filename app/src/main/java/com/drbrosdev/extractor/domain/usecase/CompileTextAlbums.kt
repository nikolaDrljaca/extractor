package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
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
import kotlinx.coroutines.flow.toList

class CompileTextAlbums(
    private val dispatcher: CoroutineDispatcher,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val searchImageByKeyword: SearchImageByKeyword,
    private val albumRepository: AlbumRepository,
    private val tokenizeText: TokenizeText,
    private val validateToken: ValidateToken
) {

    suspend operator fun invoke() {
        val allText = textEmbeddingDao.findAllTextEmbedValues()

        val tokens = tokenizeText(allText)
            .filter { validateToken(it) }
            .map { it.text }
            .toList()

        tokens
            .createFrequencyMap()
            .generateMostCommon()
            .asFlow()
            .map { topWord ->
                val params = SearchImageByKeyword.Params(
                    query = topWord,
                    keywordType = KeywordType.TEXT,
                    type = SearchType.PARTIAL,
                    dateRange = null
                )
                val result = searchImageByKeyword.execute(params)
                result to topWord
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .flowOn(dispatcher)
            .collect {
                val newAlbum = it.createNewAlbumFromPayload()
                albumRepository.createAlbum(newAlbum)
            }
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
                uri = it.uri,
                id = it.mediaImageId
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