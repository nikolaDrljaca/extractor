package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drbrosdev.extractor.data.entity.PreviousSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreviousSearchDao {

    @Query("select * from previous_search_entity")
    fun findAll(): Flow<List<PreviousSearchEntity>>

    @Query("select * from previous_search_entity where `query`=:query")
    suspend fun findByQuery(query: String): PreviousSearchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: PreviousSearchEntity)

    @Delete
    suspend fun delete(value: PreviousSearchEntity)

    @Query("delete from previous_search_entity where `query`=:query")
    suspend fun deleteByQuery(query: String)
}
