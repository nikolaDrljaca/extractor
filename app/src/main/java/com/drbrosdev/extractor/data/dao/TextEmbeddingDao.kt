package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity


@Dao
interface TextEmbeddingDao {

    @Query("SELECT count(*) FROM text_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM text_embedding WHERE id=:id")
    suspend fun findById(id: Long): TextEmbeddingEntity?

    @Query("SELECT * FROM text_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): TextEmbeddingEntity?

    @Query(
        """
        SELECT group_concat(value)
        FROM text_embedding
    """
    )
    suspend fun findAllTextEmbedValues(): String?

    @Insert
    suspend fun insert(value: TextEmbeddingEntity)

    @Insert
    suspend fun insertAll(values: List<TextEmbeddingEntity>)

    @Update
    suspend fun update(value: TextEmbeddingEntity)

    @Query("UPDATE text_embedding SET value=:value WHERE extraction_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: TextEmbeddingEntity)

    @Query("DELETE FROM text_embedding")
    suspend fun deleteAll()

    @Query("DELETE FROM text_embedding WHERE extraction_entity_id=:mediaId")
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

