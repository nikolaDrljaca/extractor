package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import com.drbrosdev.extractor.ui.components.usercollage.ExtractionCollage
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileTextAlbums(
    private val repo: ExtractorRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
    private val buildNewAlbumPayload: BuildNewAlbumPayload,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        execute(amount = 7)
            .map { (topWord, extractions) -> buildNewAlbumPayload(extractions, topWord) }
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
                val params = SearchImageByQuery.Params(
                    query = topWord,
                    keywordType = KeywordType.TEXT,
                    type = SearchType.PARTIAL,
                    dateRange = null
                )
                topWord to searchImageByQuery.execute(params)
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
    }
}