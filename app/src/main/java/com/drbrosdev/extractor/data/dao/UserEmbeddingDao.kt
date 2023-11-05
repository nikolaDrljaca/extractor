package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.UserEmbeddingEntity

@Dao
interface UserEmbeddingDao {

    @Query("select count(*) from user_embedding")
    suspend fun getCount(): Int

    @Query("select * from user_embedding where id=:id")
    suspend fun findById(id: Long): UserEmbeddingEntity?

    @Query("select * from user_embedding where image_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): UserEmbeddingEntity?

    @Insert
    suspend fun insert(value: UserEmbeddingEntity)

    @Update
    suspend fun update(value: UserEmbeddingEntity)

    @Query("UPDATE user_embedding SET value=:value WHERE image_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: UserEmbeddingEntity)

    @Query("delete from user_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)
}