package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.extraction.record.TextEmbeddingRecord


@Dao
interface TextEmbeddingDao {

    @Query("SELECT count(*) FROM text_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM text_embedding WHERE id=:id")
    suspend fun findById(id: Long): TextEmbeddingRecord?

    @Query("SELECT * FROM text_embedding WHERE extraction_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): TextEmbeddingRecord?

    @Query(
        """
        SELECT group_concat(value)
        FROM text_embedding
    """
    )
    suspend fun findAllTextEmbedValues(): String?

    @Insert
    suspend fun insert(value: TextEmbeddingRecord)

    @Insert
    suspend fun insertAll(values: List<TextEmbeddingRecord>)

    @Update
    suspend fun update(value: TextEmbeddingRecord)

    @Query("UPDATE text_embedding SET value=:value WHERE extraction_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: TextEmbeddingRecord)

    @Query("DELETE FROM text_embedding")
    suspend fun deleteAll()

    @Query("DELETE FROM text_embedding WHERE extraction_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query("""
        WITH out AS (
            SELECT value 
            FROM text_embedding AS te
            WHERE te.value IS NOT NULL AND te.value !=''
            ORDER BY random()
            LIMIT 10)
        SELECT group_concat(value)
        FROM out
    """)
    suspend fun getValueConcatAtRandom() : String?
}

