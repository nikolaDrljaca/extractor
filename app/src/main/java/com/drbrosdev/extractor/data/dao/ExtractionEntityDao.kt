package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractionEntityDao {

    //TODO: CRUD methods on ImageExtractionEntity should have their own dao
    @Query("select * from image_extraction_entity")
    fun findAll(): Flow<List<ExtractionEntity>>

    @Insert
    suspend fun insert(value: ExtractionEntity)

    @Update
    suspend fun update(value: ExtractionEntity)

    @Delete
    suspend fun delete(value: ExtractionEntity)

    @Query("delete from image_extraction_entity where media_store_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long): Int

    @Query("select count(*) from image_extraction_entity")
    suspend fun getCount(): Int
}
