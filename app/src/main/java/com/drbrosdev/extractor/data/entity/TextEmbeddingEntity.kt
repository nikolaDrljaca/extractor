package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey


@Entity(tableName = "text_embedding")
data class TextEmbeddingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "extraction_entity_id") val extractionEntityId: Long,
    val value: String
)

@Entity(tableName = "text_embedding_fts")
@Fts4(contentEntity = TextEmbeddingEntity::class)
data class TextEmbeddingFts(
    val value: String
)