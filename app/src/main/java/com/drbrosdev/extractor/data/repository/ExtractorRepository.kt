package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ExtractionEntityDao
import com.drbrosdev.extractor.data.dao.TextEmbeddingDao
import com.drbrosdev.extractor.data.dao.UserEmbeddingDao
import com.drbrosdev.extractor.data.dao.VisualEmbeddingDao
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ExtractorRepository {

    suspend fun deleteExtractionData(imageEntityId: Long)

    fun getAll(): Flow<List<ExtractionEntity>>

    suspend fun getAllIds(): Set<Long>
}

class DefaultExtractorRepository(
    private val dispatcher: CoroutineDispatcher,
    private val extractionEntityDao: ExtractionEntityDao,
    private val visualEmbeddingDao: VisualEmbeddingDao,
    private val textEmbeddingDao: TextEmbeddingDao,
    private val userEmbeddingDao: UserEmbeddingDao
): ExtractorRepository {

    override suspend fun deleteExtractionData(imageEntityId: Long) = withContext(dispatcher) {
        val countDeleted = extractionEntityDao.deleteByMediaId(imageEntityId)
        if (countDeleted == 0) return@withContext

        visualEmbeddingDao.deleteByMediaId(imageEntityId)
        textEmbeddingDao.deleteByMediaId(imageEntityId)
        userEmbeddingDao.deleteByMediaId(imageEntityId)
    }

    override fun getAll(): Flow<List<ExtractionEntity>> {
        return extractionEntityDao.findAll()
    }

    override suspend fun getAllIds(): Set<Long> {
        return extractionEntityDao.findAllIds().toSet()
    }
}