package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.SearchIndexEntity

@Dao
interface SearchIndexDao {

    @Insert
    suspend fun insert(value: SearchIndexEntity)

    @Query(
        """
        UPDATE search_index 
        SET textIndex = :value
        WHERE extraction_entity_id = :extractionEntityId
    """
    )
    suspend fun updateTextIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        UPDATE search_index 
        SET visualIndex = :value
        WHERE extraction_entity_id = :extractionEntityId
    """
    )
    suspend fun updateVisualIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        UPDATE search_index 
        SET userIndex = :value
        WHERE extraction_entity_id = :extractionEntityId
    """
    )
    suspend fun updateUserIndex(value: String, extractionEntityId: Long)

    @Query(
        """
        DELETE FROM search_index
        WHERE extraction_entity_id = :id
    """
    )
    suspend fun deleteByMediaId(id: Long)

    @Delete
    suspend fun delete(value: SearchIndexEntity)

    @Query("DELETE FROM search_index")
    suspend fun deleteAll()

    @Update
    suspend fun update(value: SearchIndexEntity)
}