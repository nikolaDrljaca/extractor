package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity


@Dao
interface TextEmbeddingDao {

    @Query("select count(*) from text_embedding")
    suspend fun getCount(): Int

    @Query("select * from text_embedding where id=:id")
    suspend fun findById(id: Long): TextEmbeddingEntity?

    @Query("select * from text_embedding where image_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): TextEmbeddingEntity?

    @Insert
    suspend fun insert(value: TextEmbeddingEntity)

    @Update
    suspend fun update(value: TextEmbeddingEntity)

    @Query("UPDATE text_embedding SET value=:value WHERE image_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: TextEmbeddingEntity)

    @Query("delete from text_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)
}

