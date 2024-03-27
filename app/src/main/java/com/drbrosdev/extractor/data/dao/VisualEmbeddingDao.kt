package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity

@Dao
interface VisualEmbeddingDao {

    @Query("SELECT count(id) FROM visual_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM visual_embedding WHERE id=:id")
    suspend fun findById(id: Long): VisualEmbeddingEntity?

    @Query("SELECT * FROM visual_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): VisualEmbeddingEntity?

    @Insert
    suspend fun insert(value: VisualEmbeddingEntity)

    @Insert
    suspend fun insertAll(values: List<VisualEmbeddingEntity>)

    @Update
    suspend fun update(value: VisualEmbeddingEntity)

    @Delete
    suspend fun delete(value: VisualEmbeddingEntity)

    @Query("DELETE FROM visual_embedding WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM visual_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query("DELETE FROM visual_embedding WHERE value=:value")
    suspend fun deleteByValue(value: String)

    @Query("""
        SELECT group_concat(value)
        FROM visual_embedding
    """)
    suspend fun findAllVisualEmbedValues(): String

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
