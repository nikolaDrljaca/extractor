package com.drbrosdev.extractor.domain.usecase.generate

import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.LupaBundle
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.toPayload
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import com.drbrosdev.extractor.domain.usecase.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.domain.usecase.token.isValidSearchToken
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList

class CompileMostCommonTextEmbeds(
    private val repo: LupaImageRepository,
    private val tokenizeText: TokenizeText,
    private val generateMostCommonTokens: GenerateMostCommonTokens,
    private val searchImageByQuery: SearchImageByQuery,
) {
    suspend fun execute(amount: Int): List<LupaBundle> {
        return compile(amount)
            .map { (topWord, extractions) ->
                LupaBundle(
                    keyword = topWord,
                    images = extractions
                )
            }
    }

    private suspend fun compile(amount: Int = 7): List<Pair<String, List<LupaImageMetadata>>> {
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
                val metadata = searchImageByQuery.execute(imageSearchParams.toPayload())
                    .map { o -> o.metadata }
                topWord to metadata
            }
            .filter { (embeddings, _) -> embeddings.isNotEmpty() }
    }
}