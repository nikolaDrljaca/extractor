package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drbrosdev.extractor.data.extraction.relation.UserExtractionRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserExtractionDao {

    @Query(
        """
        WITH out as (
            SELECT user.extraction_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT extraction.uri, extraction.media_store_id, extraction.date_added, extraction.path 
        FROM extraction JOIN out ON out.extraction_id = extraction.media_store_id
    """
    )
    @Transaction
    suspend fun findAllContaining(keyword: String): List<UserExtractionRelation>

    @Query(
        """
        WITH out as (
            SELECT user.extraction_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT extraction.uri, extraction.media_store_id, extraction.date_added, extraction.path 
        FROM extraction JOIN out ON out.extraction_id = extraction.media_store_id
    """
    )
    @Transaction
    fun findAllContainingAsFlow(keyword: String): Flow<List<UserExtractionRelation>>
}
