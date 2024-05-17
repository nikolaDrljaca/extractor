package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.AlbumConfigurationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumConfigurationDao {

    @Insert
    suspend fun insert(albumConfigurationEntity: AlbumConfigurationEntity)

    @Insert
    suspend fun insertAll(entities: List<AlbumConfigurationEntity>)

    @Query("SELECT * FROM album_configuration")
    fun findAllAsFlow(): Flow<List<AlbumConfigurationEntity>>

    suspend fun findAll(): List<AlbumConfigurationEntity> = findAllAsFlow().first()

    @Query("SELECT count(*) FROM album_configuration")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(configuration: AlbumConfigurationEntity)

    @Query("DELETE FROM album_configuration")
    suspend fun deleteAll()

    @Query("DELETE FROM album_configuration WHERE id=:configurationId")
    suspend fun deleteById(configurationId: Long)

    @Query("DELETE FROM album_configuration WHERE album_entity_id=:albumEntityId")
    suspend fun deleteByAlbumEntityId(albumEntityId: Long)

    @Update
    suspend fun update(configuration: AlbumConfigurationEntity)
}