package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.data.extraction.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.extraction.dao.UserExtractionDao
import com.drbrosdev.extractor.data.extraction.record.toExtraction
import com.drbrosdev.extractor.domain.model.UserCollage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BuildUserCollage(
    private val dispatcher: CoroutineDispatcher,
    private val userExtractionDao: UserExtractionDao,
    private val userEmbeddingDao: UserEmbeddingDao
) {

    fun invoke(): Flow<UserCollage> = flow {
        // Get all keywords, each value is CSV (one,two...), parse into unique values
        val keywords = userEmbeddingDao.findAllEmbeddingValues()
            .asSequence()
            .map { it.split(",") }
            .flatMap { it.asSequence() }
            .distinct()
            .toList()

        // Build a collage flow for each unique keyword
        keywords.forEach {
            val extractions = userExtractionDao.findAllContaining(it)
            val userCollage = UserCollage(
                userEmbed = it,
                extractions = extractions.map { relation ->
                    relation.extractionRecord.toExtraction()
                }
            )
            emit(userCollage)
        }
    }
        .flowOn(dispatcher)
}