package com.drbrosdev.extractor.domain.usecase.album

import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileVisualAlbum(
    private val repo: ExtractorRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
    private val buildNewAlbumPayload: BuildNewAlbumPayload,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        val allVisuals = repo.getAllVisualEmbedValuesAsCsv() ?: return

        val clean = allVisuals.replace(",", " ")
        val tokens = tokenizeText.invoke(clean)
            .filter { it.isValidSearchToken() }
            .toList()

        generateMostCommonTokens.invoke(tokens)
            .map {
                val topWord = it.text
                val imageSearchParams = ImageSearchParams(
                    query = topWord,
                    keywordType = KeywordType.IMAGE,
                    searchType = SearchType.PARTIAL,
                    dateRange = null
                )
                val result = searchImageByQuery.execute(imageSearchParams)
                buildNewAlbumPayload(result, topWord)
            }
            .filter { (embeds, _) -> embeds.isNotEmpty() }
            .forEach { albumRepository.createAlbum(it) }
    }
}