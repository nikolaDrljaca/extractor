package com.drbrosdev.extractor.data.album.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumConfigurationDao {

    @Insert
    suspend fun insert(albumConfigurationRecord: AlbumConfigurationRecord)

    @Insert
    suspend fun insertAll(entities: List<AlbumConfigurationRecord>)

    @Query("SELECT * FROM album_configuration")
    fun findAllAsFlow(): Flow<List<AlbumConfigurationRecord>>

    suspend fun findAll(): List<AlbumConfigurationRecord> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album_configuration")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(configuration: AlbumConfigurationRecord)

    @Query("DELETE FROM album_configuration")
    suspend fun deleteAll()

    @Query("DELETE FROM album_configuration WHERE id=:configurationId")
    suspend fun deleteById(configurationId: Long)

    @Query("DELETE FROM album_configuration WHERE album_id=:albumId")
    suspend fun deleteByAlbumId(albumId: Long)

    @Update
    suspend fun update(configuration: AlbumConfigurationRecord)
}