package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.extraction.record.ExtractionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ExtractionDao {

    @Query("SELECT * FROM extraction")
    fun findAllAsFlow(): Flow<List<ExtractionRecord>>

    suspend fun findAll(): List<ExtractionRecord> = findAllAsFlow().first()

    @Query("SELECT * FROM extraction WHERE media_store_id=:id")
    suspend fun findById(id: Long): ExtractionRecord?

    @Query("SELECT media_store_id FROM extraction")
    suspend fun findAllIds(): List<Long>

    @Insert
    suspend fun insert(value: ExtractionRecord)

    @Insert
    suspend fun insertAll(values: List<ExtractionRecord>)

    @Update
    suspend fun update(value: ExtractionRecord)

    @Delete
    suspend fun delete(value: ExtractionRecord)

    @Query("DELETE FROM extraction")
    suspend fun deleteAll()

    @Query("DELETE FROM extraction WHERE media_store_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long): Int

    @Query("SELECT count(*) FROM extraction")
    fun getCountAsFlow(): Flow<Int>

    suspend fun getCount(): Int = getCountAsFlow().first()

    @Query("""
        SELECT * 
        FROM extraction
        WHERE date_added BETWEEN :start AND :end
        ORDER BY date_added DESC
    """)
    suspend fun findByDateRange(start: String, end: String): List<ExtractionRecord>
}
