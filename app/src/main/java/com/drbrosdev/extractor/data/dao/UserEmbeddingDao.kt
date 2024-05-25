package com.drbrosdev.extractor.data.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity

@Dao
interface UserEmbeddingDao {

    @Query("SELECT count(*) FROM user_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM user_embedding WHERE id=:id")
    suspend fun findById(id: Long): UserEmbeddingEntity?

    @Query("SELECT * FROM user_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): UserEmbeddingEntity?

    @Insert
    suspend fun insert(value: UserEmbeddingEntity)

    @Update
    suspend fun update(value: UserEmbeddingEntity)

    @Query("UPDATE user_embedding SET value=:value WHERE extraction_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    suspend fun upsert(value: String, extractionEntityId: Long) {
        try {
            val userEmbedding = UserEmbeddingEntity(value = value, extractionEntityId = extractionEntityId)
            insert(userEmbedding)
        } catch (e: SQLiteConstraintException) {
            update(value, extractionEntityId)
        }
    }

    @Delete
    suspend fun delete(value: UserEmbeddingEntity)

    @Query("DELETE FROM user_embedding")
    suspend fun deleteAll()

    @Query("DELETE FROM user_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query("""
        WITH out AS (
            SELECT value 
            FROM user_embedding AS ue
            WHERE ue.value IS NOT NULL AND ue.value != ''
            ORDER BY random()
            LIMIT 10
        )
        SELECT group_concat(value)
        FROM out
    """)
    suspend fun getValueConcatAtRandom() : String?

    @Query("""
        SELECT DISTINCT value
        FROM user_embedding
        WHERE value IS NOT NULL AND value != ''
    """)
    suspend fun findAllEmbeddingValues(): List<String>
}