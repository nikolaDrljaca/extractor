package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.dao.ImageEmbeddingsDao
import com.drbrosdev.extractor.data.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation

class DefaultImageEmbeddingRepository(
    private val imageEmbeddingsDao: ImageEmbeddingsDao
) : ImageEmbeddingRepository{

    override suspend fun findBy(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation> {
        return imageEmbeddingsDao.findByLabel(strategy.query)
    }

    override suspend fun findByVisualEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation> {
        return imageEmbeddingsDao.findByVisualEmbedding(strategy.query)
    }

    override suspend fun findByTextEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation> {
        return imageEmbeddingsDao.findByTextEmbedding(strategy.query)
    }

    override suspend fun findByUserEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation> {
        return imageEmbeddingsDao.findByUserEmbedding(strategy.query)
    }
}