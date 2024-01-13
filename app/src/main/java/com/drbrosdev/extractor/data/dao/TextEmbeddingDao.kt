package com.drbrosdev.extractor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.drbrosdev.extractor.data.entity.TextEmbeddingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface TextEmbeddingDao {

    @Query("SELECT count(*) FROM text_embedding")
    suspend fun getCount(): Int

    @Query("SELECT * FROM text_embedding WHERE id=:id")
    suspend fun findById(id: Long): TextEmbeddingEntity?

    @Query("SELECT * FROM text_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun findByMediaId(mediaId: Long): TextEmbeddingEntity?

    @Query(
        """
        SELECT group_concat(value)
        FROM text_embedding
    """
    )
    suspend fun findAllTextEmbedValues(): String

    @Insert
    suspend fun insert(value: TextEmbeddingEntity)

    @Insert
    suspend fun insertAll(values: List<TextEmbeddingEntity>)

    @Update
    suspend fun update(value: TextEmbeddingEntity)

    @Query("UPDATE text_embedding SET value=:value WHERE extraction_entity_id=:imageEntityId")
    suspend fun update(value: String, imageEntityId: Long)

    @Delete
    suspend fun delete(value: TextEmbeddingEntity)

    @Query("DELETE FROM text_embedding WHERE extraction_entity_id=:mediaId")
    suspend fun deleteByMediaId(mediaId: Long)

    @Query(
        """
        SELECT t.id, t.extraction_entity_id, t.value
        FROM text_embedding AS t
        JOIN text_embedding_fts AS fts ON t.id = fts.rowid
        WHERE fts.value MATCH :query
    """
    )
    fun findByValueFtsAsFlow(query: String): Flow<List<TextEmbeddingEntity>>

    suspend fun findByValue(query: String): List<TextEmbeddingEntity> =
        findByValueFtsAsFlow(query).first()

    @Query("""
        SELECT group_concat(value)
        FROM text_embedding AS te
        WHERE te.value IS NOT NULL AND te.value != ''
        ORDER BY random()
        LIMIT :amount
    """)
    suspend fun getValueConcatAtRandom(amount: Int) : String?
}

