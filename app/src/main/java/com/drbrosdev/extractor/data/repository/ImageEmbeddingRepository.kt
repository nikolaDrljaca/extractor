package com.drbrosdev.extractor.data.repository

import com.drbrosdev.extractor.data.payload.ImageEmbeddingSearchStrategy
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation

interface ImageEmbeddingRepository {

    suspend fun findBy(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation>
    suspend fun findByVisualEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation>
    suspend fun findByTextEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation>
    suspend fun findByUserEmbed(strategy: ImageEmbeddingSearchStrategy): List<ImageEmbeddingsRelation>

}