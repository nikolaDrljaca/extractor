package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.drbrosdev.extractor.data.entity.ImageDataWithEmbeddings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface ImageDataWithEmbeddingsDao {

    @Query(
        """
            SELECT * FROM image_extraction_entity 
            LEFT JOIN text_embedding AS t ON media_store_id = t.image_entity_id 
            LEFT JOIN visual_embedding AS v ON media_store_id = v.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (t.value LIKE :query) OR
            (v.value LIKE '%' || :query || '%') OR
            (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    fun findByLabelFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByLabel(query: String) = findByLabelFlow(query).first()


    @Query(
        """
            SELECT * FROM image_extraction_entity 
            LEFT JOIN visual_embedding AS v ON media_store_id = v.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (v.value LIKE '%' || :query || '%') OR (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    fun findByVisualEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByVisualEmbedding(query: String) = findByVisualEmbeddingFlow(query).first()


    @Query(
        """
            SELECT * FROM image_extraction_entity 
            LEFT JOIN text_embedding AS t ON media_store_id = t.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (t.value LIKE :query) OR (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    fun findByTextEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByTextEmbedding(query: String) = findByTextEmbeddingFlow(query).first()


    @Query(
        """
            SELECT * FROM image_extraction_entity 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    fun findByUserEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByUserEmbedding(query: String) = findByUserEmbeddingFlow(query).first()
}