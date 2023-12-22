package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.drbrosdev.extractor.data.entity.AlbumEntity
import com.drbrosdev.extractor.data.relation.AlbumRelation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AlbumRelationDao {

//    @Query(
//        """
//        select * from album as a
//        join album_configuration as ac on a.album_id = ac.album_entity_id
//        join album_entry as ae on ae.album_entity_id = a.album_id
//        where a.album_id=:albumId
//    """
//    )
    @Query("""
        select * 
        from album as a, album_configuration, album_entry
        where a.album_id=:albumId
        group by a.album_id
    """)
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findAlbumByIdAsFlow(albumId: Long): Flow<AlbumRelation>

    suspend fun findAlbumById(albumId: Long): AlbumRelation = findAlbumByIdAsFlow(albumId).first()

    @Query("""
        select * 
        from album
        where origin=:origin
    """)
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findAllAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.USER_GENERATED): Flow<List<AlbumRelation>>

    suspend fun findAll(): List<AlbumRelation> = findAllAsFlow().first()

    @Query("""
        select * 
        from album as a, album_configuration, album_entry
        where a.origin=:origin
        group by a.album_id
    """)
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findVisualAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.VISUAL_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findVisual(): List<AlbumRelation> = findVisualAsFlow().first()


    @Query("""
        select * 
        from album as a, album_configuration, album_entry
        where a.origin=:origin
        group by a.album_id
    """)
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    fun findTextualAsFlow(origin: AlbumEntity.Origin = AlbumEntity.Origin.TEXT_COMPUTED): Flow<List<AlbumRelation>>

    suspend fun findTextual(): List<AlbumRelation> = findVisualAsFlow().first()
}