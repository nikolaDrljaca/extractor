package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.TextEmbedding
import com.drbrosdev.extractor.data.entity.VisualEmbedding


@Dao
interface TextEmbeddingDao {
    //TODO: Since this will be worked on as a flat string, think about updates
    @Insert
    suspend fun insert(value: TextEmbedding)

    @Update
    suspend fun update(value: TextEmbedding)

    @Delete
    suspend fun delete(value: TextEmbedding)
}

@Dao
interface VisualEmbeddingDao {
    @Insert
    suspend fun insert(value: VisualEmbedding)

    @Update
    suspend fun update(value: VisualEmbedding)

    @Delete
    suspend fun delete(value: VisualEmbedding)
}
