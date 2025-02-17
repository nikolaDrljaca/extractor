package com.drbrosdev.extractor.framework.logger

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Dao
interface EventLogDao {

    @Query(
        """
        WITH next AS (
            SELECT ifnull(max(event.event_order), 0) + 1
            FROM event as event
        )       
        INSERT INTO event (event_order, tag, message, timestamp, localized_message)
        VALUES ((SELECT * FROM next), :tag, :message, :timestamp, :localizedMessage)
    """
    )
    suspend fun insert(
        tag: String,
        message: String,
        timestamp: String,
        localizedMessage: String = ""
    )

    @Delete
    suspend fun delete(entry: EventRecord)

    @Query("DELETE FROM event")
    suspend fun deleteAll()

    @Query(
        """
        SELECT *
        FROM event
        ORDER BY timestamp DESC
    """
    )
    fun findAllAsFlow(): Flow<List<EventRecord>>

    suspend fun findAll() = withContext(Dispatchers.IO) { findAllAsFlow().first() }

    @Query(
        """
        SELECT count(*)
        FROM event
    """
    )
    suspend fun count(): Int

    @Query(
        """
        SELECT count(*)
        FROM event
    """
    )
    fun countAsFlow(): Flow<Int>

}
