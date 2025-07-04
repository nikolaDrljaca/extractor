package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.drbrosdev.extractor.data.extraction.relation.ImageEmbeddingsRelation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface ImageEmbeddingsDao {

    /**
     * Will match against ALL columns in [com.drbrosdev.extractor.data.search.SearchIndexRecord].
     *
     * ftsQuery can be constructed to match against certain tables of the index using:
     *
     * `<tableName1>:<query> OR <tableName2>:<query>`
     *
     * @param ftsQuery Partial matches with *, full otherwise.
     */
    @Query(
        """
        WITH lookup AS (
            SELECT si.extraction_id
            FROM search_index AS si
            JOIN search_index_fts AS fts ON fts.rowid = si.id
            WHERE search_index_fts MATCH :ftsQuery)
        SELECT im.media_store_id, im.uri, im.path, im.date_added
        FROM lupa_image AS im
        JOIN lookup ON lookup.extraction_id = im.media_store_id
        GROUP BY im.media_store_id
        ORDER BY im.date_added DESC
    """
    )
    @Transaction
    fun findByKeywordAsFlow(ftsQuery: String): Flow<List<ImageEmbeddingsRelation>>

    /**
     * Will match against ALL columns in [com.drbrosdev.extractor.data.search.SearchIndexRecord].
     *
     * ftsQuery can be constructed to match against certain tables of the index using:
     *
     * `<tableName1>:<query> OR <tableName2>:<query>`
     *
     * @param ftsQuery Partial matches with *, full otherwise.
     */
    suspend fun findByKeyword(ftsQuery: String) = findByKeywordAsFlow(ftsQuery).first()

    @Query(
        """
        SELECT DISTINCT * 
        FROM lupa_image
        WHERE media_store_id=:mediaImageId
    """
    )
    @Transaction
    fun findByMediaImageId(mediaImageId: Long): Flow<ImageEmbeddingsRelation?>

    @Query("""
        SELECT * 
        FROM lupa_image
        WHERE date_added BETWEEN :start AND :end
        ORDER BY date_added DESC
    """)
    @Transaction
    suspend fun findByDateRange(start: String, end: String): List<ImageEmbeddingsRelation>

    @Query("""
         SELECT e.*
            FROM lupa_image AS e
            ORDER BY e.rowid ASC
            LIMIT 1
    """)
    @Transaction
    fun findMostRecentAsFlow(): Flow<ImageEmbeddingsRelation?>

    @Query("""
         SELECT e.*
            FROM lupa_image AS e
            ORDER BY e.rowid ASC
            LIMIT 1
    """)
    @Transaction
    suspend fun findMostRecent(): ImageEmbeddingsRelation?

    @Query("""
        SELECT li.*
        FROM lupa_image as li
        WHERE uri = :uri
    """)
    @Transaction
    suspend fun findByUri(uri: String): ImageEmbeddingsRelation?
}