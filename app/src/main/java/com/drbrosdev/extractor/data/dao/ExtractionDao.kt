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
interface ExtractionDao {

    //TODO: CRUD methods on ImageExtractionEntity should have their own dao
    @Query("SELECT * FROM image_extraction_entity")
    fun findAllAsFlow(): Flow<List<ExtractionEntity>>

    suspend fun findAll(): List<ExtractionEntity> = findAllAsFlow().first()

    @Query("SELECT * FROM image_extraction_entity WHERE media_store_id=:id")
    suspend fun findById(id: Long): ExtractionEntity?

    @Query("SELECT media_store_id FROM image_extraction_entity")
    suspend fun findAllIds(): List<Long>

    @Insert
    suspend fun insert(value: ExtractionEntity)

    @Insert
    suspend fun insertAll(values: List<ExtractionEntity>)

    @Update
    suspend fun update(value: ExtractionEntity)

    @Delete
    suspend fun delete(value: ExtractionEntity)

    @Query("DELETE FROM image_extraction_entity WHERE media_store_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long): Int

    @Query("SELECT count(*) FROM image_extraction_entity")
    fun getCountAsFlow(): Flow<Int>

    suspend fun getCount(): Int = getCountAsFlow().first()
}
