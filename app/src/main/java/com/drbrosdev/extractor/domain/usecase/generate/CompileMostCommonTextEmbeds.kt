package com.drbrosdev.extractor.domain.usecase.generate

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionBundle
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileMostCommonTextEmbeds(
    private val repo: ExtractorRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
) {
    suspend fun execute(amount: Int): List<ExtractionBundle> {
        return compile(amount)
            .map { (topWord, extractions) ->
                ExtractionBundle(
                    keyword = topWord,
                    extractions = extractions
                )
            }
    }

    private suspend fun compile(amount: Int = 7): List<Pair<String, List<Extraction>>> {
        val allText = repo.getAllTextEmbedValuesAsCsv() ?: return emptyList()

        val tokens = tokenizeText.invoke(allText)
            .filter { it.isValidSearchToken() }
            .toList()

        return generateMostCommonTokens.execute(tokens, amount)
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
}