package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.record.toExtraction
import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.contains
import com.drbrosdev.extractor.domain.model.isDataIncomplete
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.image.BuildFtsQuery
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class DefaultSearchImageByQuery(
    private val dispatcher: CoroutineDispatcher,
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText,
    private val buildFtsQuery: BuildFtsQuery,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val trackExtractionProgress: TrackExtractionProgress,
) : SearchImageByQuery {

    override suspend fun execute(imageSearchParams: ImageSearchParams): List<Extraction> {
        // search local index
        val extractions = withContext(dispatcher) { executeQuery(imageSearchParams) }
        // check extraction status
        val extractionStatus = trackExtractionProgress.invoke().first()
        return when {
            // extraction incomplete - check stored image ids against what is actually on the device
            extractionStatus.isDataIncomplete() -> {
                val onDevice =
                    mediaStoreImageRepository.findAllById(extractions.map { it.mediaImageId.id })
                        .map { it.mediaImageId }
                        .toSet()
                extractions
                    .filter { it.mediaImageId.id in onDevice }
            }

            // extraction complete - no need to check if on device images exist
            else -> extractions
        }
    }

    private suspend fun executeQuery(imageSearchParams: ImageSearchParams): List<Extraction> {
        // clean up (tokenize) search query
        val cleanQuery = tokenizeText.invoke(imageSearchParams.query)
            .toList()
        // create fts query
        val buildFtsQueryParams = BuildFtsQuery.Params(
            tokens = cleanQuery,
            searchType = imageSearchParams.searchType,
            keywordType = imageSearchParams.keywordType
        )
        val adaptedQuery = buildFtsQuery.invoke(buildFtsQueryParams)
        // execute query and get results from DB
        val extractions = imageEmbedDao.findByKeyword(adaptedQuery.value)
            .map { it.imageEntity.toExtraction() }
        // filter if applicable
        return when {
            // if a date range is specified, filter with it
            imageSearchParams.dateRange != null -> extractions.filter {
                it.byDateRange(imageSearchParams.dateRange)
            }

            // otherwise return result
            else -> extractions
        }
    }

    private fun Extraction.byDateRange(range: DateRange?): Boolean {
        if (range == null) return true
        return dateAdded in range
    }
}