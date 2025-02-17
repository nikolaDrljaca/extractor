package com.drbrosdev.extractor.data.album

import com.drbrosdev.extractor.data.TransactionProvider
import com.drbrosdev.extractor.data.album.dao.AlbumConfigurationDao
import com.drbrosdev.extractor.data.album.dao.AlbumDao
import com.drbrosdev.extractor.data.album.dao.AlbumEntryDao
import com.drbrosdev.extractor.data.album.dao.AlbumRelationDao
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.util.toAlbum
import com.drbrosdev.extractor.util.toAlbumLabelType
import com.drbrosdev.extractor.util.toAlbumOrigin
import com.drbrosdev.extractor.util.toAlbumSearchType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultAlbumRepository(
    private val dispatcher: CoroutineDispatcher,
    private val albumEntryDao: AlbumEntryDao,
    private val albumConfigurationDao: AlbumConfigurationDao,
    private val albumDao: AlbumDao,
    private val albumRelationDao: AlbumRelationDao,
    private val runner: TransactionProvider
) : AlbumRepository {

    override suspend fun deleteAlbumById(albumId: Long) = runner.withTransaction {
        albumDao.deleteById(albumId)
        albumEntryDao.deleteByAlbumId(albumId)
        albumConfigurationDao.deleteByAlbumId(albumId)
    }

    override suspend fun deleteAlbumEntries(albumEntryIds: List<Long>) {
        albumEntryDao.deleteByIds(albumEntryIds)
    }

    override suspend fun deleteAllData() {
        runner.withTransaction {
            albumDao.deleteAll()
            albumEntryDao.deleteAll()
            albumConfigurationDao.deleteAll()
        }
    }

    override suspend fun createAlbum(newAlbum: NewAlbum) = runner.withTransaction {
        val albumId = albumDao.insert(
            AlbumRecord(
                name = newAlbum.name,
                origin = newAlbum.origin.toAlbumOrigin()
            )
        )

        val configuration = AlbumConfigurationRecord(
            albumId = albumId,
            keyword = newAlbum.keyword,
            searchType = newAlbum.searchType.toAlbumSearchType(),
            labelType = newAlbum.keywordType.toAlbumLabelType()
        )
        albumConfigurationDao.insert(configuration)

        newAlbum.entries
            .asFlow()
            .collect {
                val entry = AlbumEntryRecord(
                    albumId = albumId,
                    uri = it.uri.uri,
                    imageEntityId = it.id.id
                )
                albumEntryDao.insert(entry)
            }
    }

    override fun findAlbumByIdAsFlow(albumId: Long): Flow<Album?> {
        return albumRelationDao.findAlbumByIdAsFlow(albumId)
            .distinctUntilChanged()
            .map { it?.toAlbum() }
            .flowOn(dispatcher)
    }

    override fun getCommonTextAlbumsAsFlow(): Flow<List<Album>> {
        return albumRelationDao.findTextualAsFlow()
            .distinctUntilChanged()
            .map { it.map { entity -> entity.toAlbum() } }
            .flowOn(dispatcher)
    }

    override suspend fun getCommonTextAlbums(): List<Album> {
        val albumRelations = albumRelationDao.findTextual()
        return withContext(dispatcher) {
            albumRelations.map { it.toAlbum() }
        }
    }

    override suspend fun findAlbumById(albumId: Long): Album? {
        return albumRelationDao.findAlbumById(albumId)?.toAlbum()
    }

    override fun getCommonVisualAlbumsAsFlow(): Flow<List<Album>> {
        return albumRelationDao.findVisualAsFlow()
            .distinctUntilChanged()
            .map { it.map { entity -> entity.toAlbum() } }
            .flowOn(dispatcher)
    }

    override suspend fun getCommonVisualAlbums(): List<Album> {
        val albumRelations = albumRelationDao.findVisual()
        return withContext(dispatcher) {
            albumRelations.map { it.toAlbum() }
        }
    }

    override fun getAllUserAlbumsAsFlow(): Flow<List<Album>> {
        return albumRelationDao.findAllAsFlow()
            .distinctUntilChanged()
            .map { it.map { entity -> entity.toAlbum() } }
            .flowOn(dispatcher)
    }

    override suspend fun getAllUserAlbums(): List<Album> {
        return albumRelationDao.findAll().map { it.toAlbum() }
    }
}