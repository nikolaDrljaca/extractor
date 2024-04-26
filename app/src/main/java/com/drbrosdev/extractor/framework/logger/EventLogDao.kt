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
            FROM event_entity as event
        )       
        INSERT INTO event_entity (event_order, tag, message, timestamp, localizedMessage)
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
    suspend fun delete(entry: EventEntity)

    @Query(
        """
        SELECT *
        FROM event_entity
        ORDER BY timestamp DESC
    """
    )
    fun findAllAsFlow(): Flow<List<EventEntity>>

    suspend fun findAll() = withContext(Dispatchers.IO) { findAllAsFlow().first() }

    @Query(
        """
        SELECT count(*)
        FROM event_entity
    """
    )
    suspend fun count(): Int

}
