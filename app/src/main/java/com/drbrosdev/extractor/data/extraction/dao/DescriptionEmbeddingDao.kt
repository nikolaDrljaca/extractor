package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.extraction.record.DescriptionEmbeddingRecord

@Dao
interface DescriptionEmbeddingDao {

    @Query("SELECT count(*) from description_embedding")
    suspend fun getCount(): Int

    @Insert
    suspend fun insert(value: DescriptionEmbeddingRecord)

    @Update
    suspend fun update(value: DescriptionEmbeddingRecord)

    @Query("UPDATE description_embedding SET value=:value WHERE lupa_image_id=:lupaImageId")
    suspend fun update(value: String, lupaImageId: Long)

    @Delete
    suspend fun delete(value: DescriptionEmbeddingRecord)

    @Query("DELETE FROM description_embedding")
    suspend fun deleteAll()

    @Query("DELETE FROM description_embedding WHERE lupa_image_id=:id")
    suspend fun deleteByLupaImageId(id: Long)
}