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
            SELECT user.lupa_image_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT lupa_image.uri, lupa_image.media_store_id, lupa_image.date_added, lupa_image.path 
        FROM lupa_image JOIN out ON out.lupa_image_id = lupa_image.media_store_id
    """
    )
    @Transaction
    suspend fun findAllContaining(keyword: String): List<UserExtractionRelation>

    @Query(
        """
        WITH out as (
            SELECT user.lupa_image_id
            FROM user_embedding AS user
            WHERE user.value LIKE '%' || :keyword || '%'
        )
        SELECT lupa_image.uri, lupa_image.media_store_id, lupa_image.date_added, lupa_image.path 
        FROM lupa_image JOIN out ON out.lupa_image_id = lupa_image.media_store_id
    """
    )
    @Transaction
    fun findAllContainingAsFlow(keyword: String): Flow<List<UserExtractionRelation>>
}
