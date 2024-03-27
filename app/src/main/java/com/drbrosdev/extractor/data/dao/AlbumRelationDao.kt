package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drbrosdev.extractor.data.entity.AlbumEntity
import com.drbrosdev.extractor.data.relation.AlbumRelation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumRelationDao {

    @Query("""
        SELECT a.album_id, a.name, a.origin
        FROM album AS a
        WHERE a.album_id=:albumId
        GROUP BY a.album_id
    """)
    @Transaction
    fun findAlbumByIdAsFlow(albumId: Long): Flow<AlbumRelation?>

    suspend fun findAlbumById(albumId: Long): AlbumRelation? = findAlbumByIdAsFlow(albumId).first()

    @Query("""
        SELECT a.album_id, a.name, a.origin
        FROM album AS a
        WHERE origin=:origin
        ORDER BY album_id DESC
    """)
    @Transaction
    fun findAllAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.USER_GENERATED): Flow<List<AlbumRelation>>

    suspend fun findAll(): List<AlbumRelation> = findAllAsFlow().first()

    @Query("""
        SELECT a.album_id, a.origin, a.name
        FROM album AS a
        WHERE a.origin=:origin
        GROUP BY a.album_id
        ORDER BY a.album_id DESC 
    """)
    @Transaction
    fun findVisualAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.VISUAL_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findVisual(): List<AlbumRelation> = findVisualAsFlow().first()

    @Query("""
        SELECT a.name, a.origin, a.album_id
        FROM album AS a
        WHERE a.origin=:origin
        GROUP BY a.album_id
        ORDER BY a.album_id DESC
    """)
    @Transaction
    fun findTextualAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.TEXT_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findTextual(): List<AlbumRelation> = findVisualAsFlow().first()
}