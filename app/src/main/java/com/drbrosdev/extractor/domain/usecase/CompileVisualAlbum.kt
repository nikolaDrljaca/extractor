package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class CompileVisualAlbum(
    private val dispatcher: CoroutineDispatcher,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val albumRepository: AlbumRepository,
    private val searchImageByKeyword: SearchImageByKeyword,
    private val tokenizeText: TokenizeText,
    private val validateSuggestedToken: ValidateSuggestedSearchToken,
    private val generateMostCommonTokens: GenerateMostCommonTokens
) {

    suspend operator fun invoke() {
        val allVisuals = visualEmbeddingDao.findAllVisualEmbedValues()
            .replace(",", " ")

        val tokens = tokenizeText(allVisuals)
            .filter { validateSuggestedToken(it) }
            .flowOn(dispatcher)
            .toList()

        generateMostCommonTokens(tokens)
            .map { it.text }
            .map { topWord ->
                val params = SearchImageByKeyword.Params(
                    query = topWord,
                    keywordType = KeywordType.IMAGE,
                    type = SearchType.PARTIAL,
                    dateRange = null
                )
                val result = searchImageByKeyword.execute(params)
                result to topWord
            }
            .filter { (embeds, _) -> embeds.isNotEmpty() }
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