package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drbrosdev.extractor.data.relation.ImageDataWithEmbeddings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface ImageDataWithEmbeddingsDao {

    @Query(
        """
            SELECT DISTINCT * FROM image_extraction_entity 
            LEFT JOIN text_embedding AS t ON media_store_id = t.image_entity_id 
            LEFT JOIN visual_embedding AS v ON media_store_id = v.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (t.value LIKE '%' || :query || '%') OR
            (v.value LIKE :query) OR
            (u.value LIKE '%' || :query || '%')
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    @Transaction
    fun findByLabelFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByLabel(query: String) = findByLabelFlow(query).first()


    @Query(
        """
            SELECT DISTINCT * FROM image_extraction_entity 
            LEFT JOIN visual_embedding AS v ON media_store_id = v.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (v.value LIKE :query) OR (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    @Transaction
    fun findByVisualEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByVisualEmbedding(query: String) = findByVisualEmbeddingFlow(query).first()


    @Query(
        """
            SELECT DISTINCT * FROM image_extraction_entity 
            LEFT JOIN text_embedding AS t ON media_store_id = t.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (t.value LIKE '%' || :query || '%') OR (u.value LIKE :query)
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    @Transaction
    fun findByTextEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByTextEmbedding(query: String) = findByTextEmbeddingFlow(query).first()


    @Query(
        """
            SELECT DISTINCT * FROM image_extraction_entity 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            WHERE (u.value LIKE '%' || :query || '%')
            GROUP BY image_extraction_entity.media_store_id
    """
    )
    @Transaction
    fun findByUserEmbeddingFlow(query: String): Flow<List<ImageDataWithEmbeddings>>

    suspend fun findByUserEmbedding(query: String) = findByUserEmbeddingFlow(query).first()

    @Query("""
            SELECT DISTINCT * FROM image_extraction_entity 
            LEFT JOIN text_embedding AS t ON media_store_id = t.image_entity_id 
            LEFT JOIN user_embedding AS u ON media_store_id = u.image_entity_id 
            LEFT JOIN visual_embedding as v on media_store_id = v.image_entity_id
            WHERE media_store_id=:mediaImageId
    """)
    @Transaction
    fun findByMediaImageId(mediaImageId: Long): Flow<ImageDataWithEmbeddings?>
}