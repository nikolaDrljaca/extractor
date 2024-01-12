package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface VisualEmbeddingDao {

    data class VisualEmbedUsage(
        val value: String,
        val usageCount: Int
    )

    @Query("SELECT count(id) FROM visual_embedding")
    suspend fun getCount(): Int

    @Query("""
        SELECT value, count(*) AS usageCount
        FROM visual_embedding
        GROUP BY value
        ORDER BY usageCount DESC
        LIMIT :amount
    """)
    fun getMostUsedAsFlow(amount: Int): Flow<List<VisualEmbedUsage>>

    suspend fun getMostUsed(amount: Int) = getMostUsedAsFlow(amount).first()

    @Query("SELECT * FROM visual_embedding WHERE id=:id")
    suspend fun findById(id: Long): VisualEmbeddingEntity?

    @Query("SELECT * FROM visual_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): List<VisualEmbeddingEntity>

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
}
