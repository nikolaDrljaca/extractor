package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.TextEmbedding
import com.drbrosdev.extractor.data.entity.VisualEmbedding


@Dao
interface TextEmbeddingDao {

    @Query("select count(*) from text_embedding")
    suspend fun getCount(): Int

    @Query("select * from text_embedding where id=:id")
    suspend fun findById(id: Long): TextEmbedding?

    @Query("select * from text_embedding where image_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): TextEmbedding?

    @Insert
    suspend fun insert(value: TextEmbedding)

    @Update
    suspend fun update(value: TextEmbedding)

    @Query("UPDATE text_embedding SET value=:value WHERE image_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: TextEmbedding)

    @Query("delete from text_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)
}

@Dao
interface VisualEmbeddingDao {

    @Query("select count(id) from visual_embedding")
    suspend fun getCount(): Int

    @Query("select * from visual_embedding where id=:id")
    suspend fun findById(id: Long): VisualEmbedding?

    @Query("select * from visual_embedding where id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): List<VisualEmbedding>

    @Insert
    suspend fun insert(value: VisualEmbedding)

    @Update
    suspend fun update(value: VisualEmbedding)

    @Delete
    suspend fun delete(value: VisualEmbedding)

    @Query("delete from visual_embedding where image_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)
}
