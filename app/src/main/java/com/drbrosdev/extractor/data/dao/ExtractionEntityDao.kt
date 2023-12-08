package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ExtractionEntityDao {

    //TODO: CRUD methods on ImageExtractionEntity should have their own dao
    @Query("select * from image_extraction_entity")
    fun findAllAsFlow(): Flow<List<ExtractionEntity>>

    suspend fun findAll(): List<ExtractionEntity> = findAllAsFlow().first()

    @Query("select * from image_extraction_entity where media_store_id=:id")
    suspend fun findById(id: Long): ExtractionEntity?

    @Query("select media_store_id from image_extraction_entity")
    suspend fun findAllIds(): List<Long>

    @Insert
    suspend fun insert(value: ExtractionEntity)

    @Insert
    suspend fun insertAll(values: List<ExtractionEntity>)

    @Update
    suspend fun update(value: ExtractionEntity)

    @Delete
    suspend fun delete(value: ExtractionEntity)

    @Query("delete from image_extraction_entity where media_store_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long): Int

    @Query("select count(*) from image_extraction_entity")
    suspend fun getCount(): Int
}
