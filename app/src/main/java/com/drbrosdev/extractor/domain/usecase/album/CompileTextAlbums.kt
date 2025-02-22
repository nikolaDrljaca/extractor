package com.drbrosdev.extractor.domain.usecase.album

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

class CompileTextAlbums(
    private val repo: ExtractorRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
    private val buildNewAlbumPayload: BuildNewAlbumPayload,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        val allText = repo.getAllTextEmbedValuesAsCsv() ?: return

        val tokens = tokenizeText.invoke(allText)
            .filter { it.isValidSearchToken() }
            .toList()

        generateMostCommonTokens(tokens)
            .map {
                val topWord = it.text
                val params = SearchImageByQuery.Params(
                    query = topWord,
                    keywordType = KeywordType.TEXT,
                    type = SearchType.PARTIAL,
                    dateRange = null
                )
                val result = searchImageByQuery.execute(params)
                buildNewAlbumPayload(result, topWord)
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
            .forEach { albumRepository.createAlbum(it) }
    }
}