package com.drbrosdev.extractor.data.extraction.dao

import androidx.room.Dao
import androidx.room.Query
import com.drbrosdev.extractor.data.extraction.relation.ExtractionDataRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractionDataDao {

    @Query("""
         SELECT e.*
            FROM extraction AS e
            ORDER BY e.date_added DESC
            LIMIT 1
    """)
    fun findMostRecentAsFlow(): Flow<ExtractionDataRelation?>

    @Query("""
         SELECT e.*
            FROM extraction AS e
            ORDER BY e.rowid ASC
            LIMIT 1
    """)
    suspend fun findMostRecent(): ExtractionDataRelation?
}