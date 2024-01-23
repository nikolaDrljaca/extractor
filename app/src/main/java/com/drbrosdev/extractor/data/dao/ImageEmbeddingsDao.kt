package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface ImageEmbeddingsDao {

    /**
     * TEXT, USER -> search with FTS tables, use replace() to change % to * for partial searches,
     * compatible with MATCH
     *
     * VISUAL -> search with LIKE, uses % for partial searches
     */
    @Query(
        """
        WITH text_embeds AS (
            SELECT t.id, t.extraction_entity_id, t.value
            FROM text_embedding AS t
            JOIN text_embedding_fts AS fts ON t.id = fts.rowid
            WHERE fts.value MATCH replace(:query, '%', '*')
        ),
        user_embeds AS (
            SELECT u.id, u.extraction_entity_id, u.value
            FROM user_embedding AS u
            JOIN user_embedding_fts AS fts ON u.id = fts.rowid
            WHERE fts.value MATCH replace(:query, '%', '*')
        ),
        visual_embeds AS (
            SELECT v.id, v.extraction_entity_id, v.value 
            FROM visual_embedding as v
            WHERE v.value LIKE :query
        ), 
        result_set AS (
            SELECT * FROM text_embeds AS te
            UNION
            SELECT * FROM user_embeds AS ue
            UNION 
            SELECT * FROM visual_embeds AS ve
        )
        SELECT DISTINCT * FROM image_extraction_entity
        JOIN result_set ON result_set.extraction_entity_id = image_extraction_entity.media_store_id
        GROUP BY image_extraction_entity.media_store_id
        ORDER BY image_extraction_entity.date_added DESC
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByKeywordAsFlow(query: String): Flow<List<ImageEmbeddingsRelation>>

    suspend fun findByKeyword(query: String) = findByKeywordAsFlow(query).first()

    @Query(
        """
        SELECT * 
        FROM visual_embedding AS ve
        JOIN image_extraction_entity AS im ON im.media_store_id = ve.extraction_entity_id
        WHERE ve.value LIKE :query
        GROUP BY im.media_store_id
        ORDER BY im.date_added DESC
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByVisualEmbeddingFlow(query: String): Flow<List<ImageEmbeddingsRelation>>

    suspend fun findByVisualEmbedding(query: String) = findByVisualEmbeddingFlow(query).first()

    @Query(
        """
        WITH text_embeds AS (
            SELECT *
            FROM text_embedding AS t
            JOIN text_embedding_fts AS fts ON t.id = fts.rowid
            WHERE fts.value MATCH replace(:query, '%', '*')
        )
        
        SELECT DISTINCT * FROM image_extraction_entity AS im
        JOIN text_embeds AS t ON im.media_store_id = t.extraction_entity_id
        WHERE t.value IS NOT NULL
        GROUP BY im.media_store_id
        ORDER BY im.date_added DESC
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByTextEmbeddingFtsAsFlow(query: String): Flow<List<ImageEmbeddingsRelation>>

    suspend fun findByTextEmbeddingFts(query: String) = findByTextEmbeddingFtsAsFlow(query).first()

    @Query(
        """
        WITH user_embeds AS (
            SELECT *
            FROM user_embedding AS u
            JOIN user_embedding_fts AS fts ON u.id = fts.rowid
            WHERE fts.value MATCH replace(:query, '%', '*')
        )
        
        SELECT DISTINCT * FROM image_extraction_entity AS im
        LEFT JOIN user_embeds AS u ON im.media_store_id = u.extraction_entity_id
        WHERE u.value IS NOT NULL
        GROUP BY im.media_store_id
        ORDER BY im.date_added DESC
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByUserEmbeddingFtsAsFlow(query: String): Flow<List<ImageEmbeddingsRelation>>

    suspend fun findByUserEmbeddingFts(query: String) = findByUserEmbeddingFtsAsFlow(query).first()

    @Query(
        """
        SELECT DISTINCT * 
        FROM image_extraction_entity 
        WHERE media_store_id=:mediaImageId
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByMediaImageId(mediaImageId: Long): Flow<ImageEmbeddingsRelation?>
}