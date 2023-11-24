package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.VisualEmbeddingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisualEmbeddingDao {

    data class VisualEmbedUsage(
        val value: String,
        val usageCount: Int
    )

    @Query("select count(id) from visual_embedding")
    suspend fun getCount(): Int

    @Query("select value, count(*) as usageCount from visual_embedding group by value order by usageCount desc limit :amount")
    fun getMostUsed(amount: Int): Flow<List<VisualEmbedUsage>>

    @Query("select * from visual_embedding where id=:id")
    suspend fun findById(id: Long): VisualEmbeddingEntity?

    @Query("select * from visual_embedding where id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): List<VisualEmbeddingEntity>

    @Insert
    suspend fun insert(value: VisualEmbeddingEntity)

    @Insert
    suspend fun insertAll(vararg embeds: VisualEmbeddingEntity)

    @Update
    suspend fun update(value: VisualEmbeddingEntity)

    @Delete
    suspend fun delete(value: VisualEmbeddingEntity)

    @Query("delete from visual_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query("delete from visual_embedding where value=:value")
    suspend fun deleteByValue(value: String)
}
