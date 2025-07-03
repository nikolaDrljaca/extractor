package com.drbrosdev.extractor.data.album.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drbrosdev.extractor.data.album.AlbumRelation
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumRelationDao {

    @Query("""
        SELECT a.id, a.name, a.origin
        FROM album AS a
        WHERE a.id=:albumId
        GROUP BY a.id
    """)
    @Transaction
    fun findAlbumByIdAsFlow(albumId: Long): Flow<AlbumRelation?>

    suspend fun findAlbumById(albumId: Long): AlbumRelation? = findAlbumByIdAsFlow(albumId).first()

    @Query("""
        SELECT a.id, a.name, a.origin
        FROM album AS a
        WHERE origin=:origin
        ORDER BY id DESC
    """)
    @Transaction
    fun findAllAsFlow(origin: AlbumRecord.Origin = AlbumRecord.Origin.USER_GENERATED): Flow<List<AlbumRelation>>

    suspend fun findAll(): List<AlbumRelation> = findAllAsFlow().first()

    @Query("""
        SELECT a.id, a.origin, a.name
        FROM album AS a
        WHERE a.origin=:origin
        GROUP BY a.id
        ORDER BY a.id DESC 
    """)
    @Transaction
    fun findVisualAsFlow(origin: AlbumRecord.Origin = AlbumRecord.Origin.VISUAL_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findVisual(): List<AlbumRelation> = findVisualAsFlow().first()

    @Query("""
        SELECT a.name, a.origin, a.id
        FROM album AS a
        WHERE a.origin=:origin
        GROUP BY a.id
        ORDER BY a.id DESC
    """)
    @Transaction
    fun findTextualAsFlow(origin: AlbumRecord.Origin = AlbumRecord.Origin.TEXT_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findTextual(): List<AlbumRelation> = findVisualAsFlow().first()
}