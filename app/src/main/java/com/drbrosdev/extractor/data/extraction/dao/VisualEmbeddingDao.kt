package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.extraction.record.VisualEmbeddingRecord

@Dao
interface VisualEmbeddingDao {

    @Query("SELECT count(id) FROM visual_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM visual_embedding WHERE id=:id")
    suspend fun findById(id: Long): VisualEmbeddingRecord?

    @Query("SELECT * FROM visual_embedding WHERE extraction_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): VisualEmbeddingRecord?

    @Insert
    suspend fun insert(value: VisualEmbeddingRecord)

    @Insert
    suspend fun insertAll(values: List<VisualEmbeddingRecord>)

    @Update
    suspend fun update(value: VisualEmbeddingRecord)

    @Delete
    suspend fun delete(value: VisualEmbeddingRecord)

    @Query("DELETE FROM visual_embedding")
    suspend fun deleteAll()

    @Query("DELETE FROM visual_embedding WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM visual_embedding WHERE extraction_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query("DELETE FROM visual_embedding WHERE value=:value")
    suspend fun deleteByValue(value: String)

    @Query("""
        SELECT group_concat(value)
        FROM visual_embedding
    """)
    suspend fun findAllVisualEmbedValues(): String?

    @Query("""
        WITH result AS (
            SELECT value
            FROM visual_embedding AS ve
            WHERE ve.value IS NOT NULL AND ve.value != ''
            ORDER BY random()
            LIMIT 10)
        SELECT group_concat(value)
        FROM result
    """)
    suspend fun getValuesAtRandom(): String?
}
