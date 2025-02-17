package com.drbrosdev.extractor.domain.repository

import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    suspend fun deleteAllData()

    suspend fun createAlbum(newAlbum: NewAlbum)

    fun findAlbumByIdAsFlow(albumId: Long): Flow<Album?>

    suspend fun findAlbumById(albumId: Long): Album?

    fun getCommonVisualAlbumsAsFlow(): Flow<List<Album>>

    suspend fun getCommonVisualAlbums(): List<Album>

    fun getCommonTextAlbumsAsFlow(): Flow<List<Album>>

    suspend fun getCommonTextAlbums(): List<Album>

    fun getAllUserAlbumsAsFlow(): Flow<List<Album>>

    suspend fun getAllUserAlbums(): List<Album>

    suspend fun deleteAlbumById(albumId: Long)

    suspend fun deleteAlbumEntries(albumEntryIds: List<Long>)
}

