package com.drbrosdev.extractor.data.album.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumEntryDao {

    @Insert
    suspend fun insert(entry: AlbumEntryRecord)

    @Insert
    suspend fun insertAll(entities: List<AlbumEntryRecord>)

    @Query("SELECT * FROM album_entry")
    fun findAllAsFlow(): Flow<List<AlbumEntryRecord>>

    suspend fun findAll(): List<AlbumEntryRecord> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album_entry")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(entry: AlbumEntryRecord)

    @Query("DELETE FROM album_entry")
    suspend fun deleteAll()

    @Query("DELETE FROM album_entry WHERE id=:entryId")
    suspend fun deleteById(entryId: Long)

    @Query("DELETE FROM album_entry WHERE album_id=:albumEntityId")
    suspend fun deleteByAlbumId(albumEntityId: Long)

    @Query("""
        DELETE FROM album_entry
        WHERE id IN (:entryIds)
    """)
    suspend fun deleteByIds(entryIds: List<Long>)

    @Update
    suspend fun update(entry: AlbumEntryRecord)
}