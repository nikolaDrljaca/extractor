package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.extraction.record.LupaImageMetadataRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ExtractionDao {

    @Query("SELECT * FROM lupa_image")
    fun findAllAsFlow(): Flow<List<LupaImageMetadataRecord>>

    suspend fun findAll(): List<LupaImageMetadataRecord> = findAllAsFlow().first()

    @Query("SELECT * FROM lupa_image WHERE media_store_id=:id")
    suspend fun findById(id: Long): LupaImageMetadataRecord?

    @Query("SELECT media_store_id FROM lupa_image")
    suspend fun findAllIds(): List<Long>

    @Insert
    suspend fun insert(value: LupaImageMetadataRecord)

    @Insert
    suspend fun insertAll(values: List<LupaImageMetadataRecord>)

    @Update
    suspend fun update(value: LupaImageMetadataRecord)

    @Delete
    suspend fun delete(value: LupaImageMetadataRecord)

    @Query("DELETE FROM lupa_image")
    suspend fun deleteAll()

    @Query("DELETE FROM lupa_image WHERE media_store_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long): Int

    @Query("SELECT count(*) FROM lupa_image")
    fun getCountAsFlow(): Flow<Int>

    suspend fun getCount(): Int = getCountAsFlow().first()

    @Query("""
        SELECT * 
        FROM lupa_image
        WHERE date_added BETWEEN :start AND :end
        ORDER BY date_added DESC
    """)
    suspend fun findByDateRange(start: String, end: String): List<LupaImageMetadataRecord>
}
