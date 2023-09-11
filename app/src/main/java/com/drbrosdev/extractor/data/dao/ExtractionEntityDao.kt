package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.ExtractionEntity

@Dao
interface ExtractionEntityDao {

    //TODO: CRUD methods on ImageExtractionEntity should have their own dao
    @Insert
    suspend fun insert(value: ExtractionEntity)

    @Update
    suspend fun update(value: ExtractionEntity)

    @Delete
    suspend fun delete(value: ExtractionEntity)
}
