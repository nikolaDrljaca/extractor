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

    @Query("select * from album_configuration")
    fun findAllAsFlow(): Flow<List<AlbumConfigurationEntity>>

    suspend fun findAll(): List<AlbumConfigurationEntity> = findAllAsFlow().first()

    @Query("select count(*) from album_configuration")
    fun getCountAsFlow(): Flow<Long>

    suspend fun getCount(): Long = getCountAsFlow().first()

    @Delete
    suspend fun delete(configuration: AlbumConfigurationEntity)

    @Query("delete from album_configuration where id=:configurationId")
    suspend fun deleteById(configurationId: Long)

    @Update
    suspend fun update(configuration: AlbumConfigurationEntity)
}