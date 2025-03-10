package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionCollage
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileTextAlbums(
    private val repo: ExtractorRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        execute(amount = 7)
            .map { (topWord, extractions) ->
                buildNewAlbumPayload(extractions, topWord)
                    .copy(keywordType = KeywordType.TEXT)
            }
            .forEach { albumRepository.createAlbum(it) }
    }

    suspend operator fun invoke(amount: Int): List<ExtractionCollage> {
        return execute(amount)
            .map { (topWord, extractions) ->
                ExtractionCollage(
                    keyword = topWord,
                    extractions = extractions
                )
            }
    }

    private suspend fun execute(amount: Int = 7): List<Pair<String, List<Extraction>>> {
        val allText = repo.getAllTextEmbedValuesAsCsv() ?: return emptyList()

        val tokens = tokenizeText.invoke(allText)
            .filter { it.isValidSearchToken() }
            .toList()

        return generateMostCommonTokens(tokens, amount)
            .map {
                val topWord = it.text
                val imageSearchParams = ImageSearchParams(
                    query = topWord,
                    keywordType = KeywordType.TEXT,
                    searchType = SearchType.PARTIAL,
                    dateRange = null
                )
                topWord to searchImageByQuery.execute(imageSearchParams)
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
    }

    private fun buildNewAlbumPayload(
        extractions: List<Extraction>,
        searchTerm: String
    ): NewAlbum {
        val entries = extractions.map {
            NewAlbum.Entry(
                uri = it.uri,
                id = it.mediaImageId
            )
        }
        return NewAlbum(
            keyword = searchTerm,
            name = searchTerm,
            searchType = SearchType.PARTIAL,
            keywordType = KeywordType.TEXT,
            entries = entries,
            origin = NewAlbum.Origin.TEXT_COMPUTED
        )
    }
}