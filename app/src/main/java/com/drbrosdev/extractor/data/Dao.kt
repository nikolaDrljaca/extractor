package com.drbrosdev.extractor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ImageDataDao {
    @Query("select * from image_data_entity")
    fun getAll(): Flow<List<ImageDataEntity>>

    @Query("select * from image_data_entity where labels like :query")
    fun findByLabel(query: String): Flow<List<ImageDataEntity>>

    @Insert
    fun insert(vararg items: ImageDataEntity)

    @Delete
    fun delete(value: ImageDataEntity)
}