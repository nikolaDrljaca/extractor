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

    @Query("SELECT * FROM album_entry")
    fun findAllAsFlow(): Flow<List<AlbumEntryEntity>>

    suspend fun findAll(): List<AlbumEntryEntity> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album_entry")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(entry: AlbumEntryEntity)

    @Query("DELETE FROM album_entry")
    suspend fun deleteAll()

    @Query("DELETE FROM album_entry WHERE id=:entryId")
    suspend fun deleteById(entryId: Long)

    @Query("DELETE FROM album_entry WHERE album_entity_id=:albumEntityId")
    suspend fun deleteByAlbumEntityId(albumEntityId: Long)

    @Query("""
        DELETE FROM album_entry
        WHERE id IN (:entryIds)
    """)
    suspend fun deleteByIds(entryIds: List<Long>)

    @Update
    suspend fun update(entry: AlbumEntryEntity)
}