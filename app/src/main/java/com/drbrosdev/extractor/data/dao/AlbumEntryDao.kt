package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.AlbumEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumEntryDao {

    @Insert
    suspend fun insert(entry: AlbumEntryEntity)

    @Insert
    suspend fun insertAll(entities: List<AlbumEntryEntity>)

    @Query("select * from album_entry")
    fun findAllAsFlow(): Flow<List<AlbumEntryEntity>>

    suspend fun findAll(): List<AlbumEntryEntity> = findAllAsFlow().first()

    @Query("select count(*) from album_entry")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(entry: AlbumEntryEntity)

    @Query("delete from album_entry where id=:entryId")
    suspend fun deleteById(entryId: Long)

    @Update
    suspend fun update(entry: AlbumEntryEntity)
}