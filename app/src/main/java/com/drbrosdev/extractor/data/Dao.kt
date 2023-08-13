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

    @Query("select * from image_data_entity where labels like '%' || :query || '%'")
    fun findByLabel(query: String): Flow<List<ImageDataEntity>>

    @Insert
    suspend fun insert(vararg items: ImageDataEntity)

    @Delete
    suspend fun delete(value: ImageDataEntity)

    @Query("delete from image_data_entity where media_store_id=:mediaStoreId")
    suspend fun deleteByMediaId(mediaStoreId: Long)
}