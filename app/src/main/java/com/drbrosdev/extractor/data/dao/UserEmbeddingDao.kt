package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.UserEmbedding

@Dao
interface UserEmbeddingDao {

    @Query("select count(*) from user_embedding")
    suspend fun getCount(): Int

    @Query("select * from user_embedding where id=:id")
    suspend fun findById(id: Long): UserEmbedding?

    @Query("select * from user_embedding where image_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): UserEmbedding?

    @Insert
    suspend fun insert(value: UserEmbedding)

    @Update
    suspend fun update(value: UserEmbedding)

    @Delete
    suspend fun delete(value: UserEmbedding)

    @Query("delete from user_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)
}