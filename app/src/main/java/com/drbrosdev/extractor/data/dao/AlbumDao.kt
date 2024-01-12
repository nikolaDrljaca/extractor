package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumDao {
    @Insert
    suspend fun insert(albumEntity: AlbumEntity): Long

    @Insert
    suspend fun insertAll(entities: List<AlbumEntity>)

    @Query("SELECT * FROM album")
    fun findAllAsFlow(): Flow<List<AlbumEntity>>

    suspend fun findAll(): List<AlbumEntity> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(albumEntity: AlbumEntity)

    @Query("DELETE FROM album WHERE album_id=:albumId")
    suspend fun deleteById(albumId: Long)

    @Update
    suspend fun update(albumEntity: AlbumEntity)
}


