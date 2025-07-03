package com.drbrosdev.extractor.data.album.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumDao {
    @Insert
    suspend fun insert(albumRecord: AlbumRecord): Long

    @Insert
    suspend fun insertAll(entities: List<AlbumRecord>)

    @Query("SELECT * FROM album")
    fun findAllAsFlow(): Flow<List<AlbumRecord>>

    suspend fun findAll(): List<AlbumRecord> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(albumRecord: AlbumRecord)

    @Query("DELETE FROM album")
    suspend fun deleteAll()

    @Query("DELETE FROM album WHERE id=:albumId")
    suspend fun deleteById(albumId: Long)

    @Update
    suspend fun update(albumRecord: AlbumRecord)
}


