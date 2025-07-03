package com.drbrosdev.extractor.data.search

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchIndexDao {

    @Insert
    suspend fun insert(value: SearchIndexRecord)

    @Query(
        """
        UPDATE search_index 
        SET text_index = :value
        WHERE extraction_id = :extractionEntityId
    """
    )
    suspend fun updateTextIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        UPDATE search_index 
        SET visual_index = :value
        WHERE extraction_id = :extractionEntityId
    """
    )
    suspend fun updateVisualIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        UPDATE search_index 
        SET user_index = :value
        WHERE extraction_id = :extractionEntityId
    """
    )
    suspend fun updateUserIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        DELETE FROM search_index
        WHERE extraction_id = :id
    """
    )
    suspend fun deleteByMediaId(id: Long)

    @Delete
    suspend fun delete(value: SearchIndexRecord)

    @Query("DELETE FROM search_index")
    suspend fun deleteAll()

    @Update
    suspend fun update(value: SearchIndexRecord)
}