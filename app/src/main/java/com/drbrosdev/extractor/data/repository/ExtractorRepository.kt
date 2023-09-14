package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ExtractorRepository {

    suspend fun deleteExtractionData(imageEntityId: Long)

    fun getAll(): Flow<List<ExtractionEntity>>
}

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionEntityDao: ExtractionEntityDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao
): ExtractorRepository {

    override suspend fun deleteExtractionData(imageEntityId: Long) = withContext(dispatcher) {
        val countDeleted = extractionEntityDao.deleteByMediaId(imageEntityId)
        if (countDeleted == 0) return@withContext

        visualEmbeddingDao.deleteByMediaId(imageEntityId)
        textEmbeddingDao.deleteByMediaId(imageEntityId)
    }

    override fun getAll(): Flow<List<ExtractionEntity>> {
        return extractionEntityDao.findAll()
    }
}