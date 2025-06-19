package com.drbrosdev.extractor.domain.usecase.search

import com.drbrosdev.extractor.data.extraction.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.extraction.relation.toLupaImage
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.contains
import com.drbrosdev.extractor.domain.model.isDataIncomplete
import com.drbrosdev.extractor.domain.model.search.FtsQuery
import com.drbrosdev.extractor.domain.model.search.SearchPayload
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.isBlank
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList

class SearchImageByQuery(
    private val imageEmbedDao: ImageEmbeddingsDao,
    private val tokenizeText: TokenizeText,
    private val buildFtsQuery: BuildFtsQuery,
    private val mediaStoreImageRepository: MediaStoreImageRepository,
    private val trackExtractionProgress: TrackExtractionProgress,
) {
    suspend fun execute(payload: SearchPayload): List<LupaImage> {
        // consider if payload is empty
        if (payload.isBlank()) {
            return emptyList()
        }
        val result = when (payload) {
            is SearchPayload.OnlyDate -> {
                val start = payload.dateRange.start.toString()
                val end = payload.dateRange.end.toString()
                imageEmbedDao.findByDateRange(start, end)
            }

            is SearchPayload.OnlyQuery -> {
                val adaptedQuery = with(payload) {
                    prepareQuery(query, keywordType, searchType)
                }
                imageEmbedDao.findByKeyword(adaptedQuery.value)
            }

            is SearchPayload.Full -> {
                val adaptedQuery = with(payload) {
                    prepareQuery(query, keywordType, searchType)
                }
                imageEmbedDao.findByKeyword(adaptedQuery.value)
            }
        }
        val checkOnDevice = createOnDeviceCheck(result.map { it.imageEntity.mediaStoreId })
        return result.asSequence()
            .filter {
                when (payload) {
                    is SearchPayload.OnlyDate -> checkOnDevice(it.imageEntity.mediaStoreId)
                    is SearchPayload.OnlyQuery -> checkOnDevice(it.imageEntity.mediaStoreId)
                    is SearchPayload.Full -> checkOnDevice(it.imageEntity.mediaStoreId)
                            && it.imageEntity.dateAdded in payload.dateRange
                }
            }
            .map { it.toLupaImage() }
            .toList()
    }

    private suspend fun createOnDeviceCheck(inStorageIds: List<Long>): (Long) -> Boolean {
        val progress = trackExtractionProgress.invoke()
            .first()
        val onDeviceLookup = mediaStoreImageRepository.findAllById(inStorageIds)
            .map { it.mediaImageId }
            .toSet()
        return when {
            progress.isDataIncomplete() -> { inStorage -> inStorage in onDeviceLookup }
            else -> { _ -> true }
        }
    }

    private suspend fun prepareQuery(
        query: String,
        keywordType: KeywordType,
        searchType: SearchType
    ): FtsQuery {
        // clean query
        val tokens = tokenizeText.invoke(query)
            .toList()
        // build fts query
        return buildFtsQuery.invoke(
            BuildFtsQuery.Params(
                tokens = tokens,
                searchType = searchType,
                keywordType = keywordType
            )
        )
    }
}