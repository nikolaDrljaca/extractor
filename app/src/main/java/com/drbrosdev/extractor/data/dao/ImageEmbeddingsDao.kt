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
            WITH out AS (
                SELECT t.id, t.extraction_entity_id, t.value
                FROM text_embedding AS t
                JOIN text_embedding_fts AS fts ON t.id = fts.rowid
                WHERE fts.value MATCH :ftsQuery
            )
            SELECT im.media_store_id, im.path, im.uri, im.date_added FROM image_extraction_entity AS im
            JOIN out ON out.extraction_entity_id = im.media_store_id
            GROUP BY im.media_store_id
        ),
        user_embeds AS (
            WITH out AS (
                SELECT u.id, u.extraction_entity_id, u.value
                FROM user_embedding AS u
                JOIN user_embedding_fts AS fts ON u.id = fts.rowid
                WHERE fts.value MATCH :ftsQuery
            )
            SELECT im.media_store_id, im.path, im.uri, im.date_added FROM image_extraction_entity AS im
            JOIN out ON out.extraction_entity_id = im.media_store_id
            GROUP BY im.media_store_id
        ),
        visual_embeds AS (
            WITH initial AS (
                SELECT ve.extraction_entity_id, ve.value
                FROM visual_embedding AS ve
                ORDER BY ve.value
            ),
            lookup AS (
                SELECT ve.extraction_entity_id AS entity_id, group_concat(ve.value) AS all_values 
                FROM initial AS ve 
                GROUP BY entity_id
            )
            SELECT DISTINCT im.media_store_id, im.path, im.uri, im.date_added FROM image_extraction_entity AS im
            JOIN lookup ON lookup.entity_id = im.media_store_id
            WHERE lookup.all_values LIKE :visualQuery
            GROUP BY im.media_store_id
            ORDER BY im.date_added DESC
        ), 
        result_set AS (
            SELECT * FROM text_embeds AS te
            UNION
            SELECT * FROM user_embeds AS ue
            UNION 
            SELECT * FROM visual_embeds AS ve
        )
        SELECT DISTINCT * FROM result_set
        ORDER BY result_set.date_added DESC
    """
    )
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findByKeywordAsFlow(visualQuery: String, ftsQuery: String): Flow<List<ImageEmbeddingsRelation>>

    suspend fun findByKeyword(visualQuery: String, ftsQuery: String) = findByKeywordAsFlow(visualQuery, ftsQuery).first()

    @Query(
        """
        WITH initial AS (
            SELECT ve.extraction_entity_id, ve.value
            FROM visual_embedding AS ve
            ORDER BY ve.value
        ),
        lookup AS (
            SELECT ve.extraction_entity_id AS entity_id, group_concat(ve.value) AS all_values 
            FROM initial AS ve
            GROUP BY entity_id
        )
        SELECT DISTINCT im.media_store_id, im.uri, im.date_added, im.path from image_extraction_entity AS im
        JOIN lookup ON lookup.entity_id = im.media_store_id
        WHERE lookup.all_values LIKE :query
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
            WHERE fts.value MATCH :query
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
            WHERE fts.value MATCH :query
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