package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.drbrosdev.extractor.data.relation.UserExtractionRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserExtractionDao {

    @Query(
        """
        WITH out as (
            SELECT user.extraction_entity_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT extraction.uri, extraction.media_store_id, extraction.date_added, extraction.path 
        FROM image_extraction_entity AS extraction JOIN out ON out.extraction_entity_id = extraction.media_store_id
    """
    )
    suspend fun findAllContaining(keyword: String): List<UserExtractionRelation>

    @Query(
        """
        WITH out as (
            SELECT user.extraction_entity_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT extraction.uri, extraction.media_store_id, extraction.date_added, extraction.path 
        FROM image_extraction_entity AS extraction JOIN out ON out.extraction_entity_id = extraction.media_store_id
    """
    )
    fun findAllContainingAsFlow(keyword: String): Flow<List<UserExtractionRelation>>
}
