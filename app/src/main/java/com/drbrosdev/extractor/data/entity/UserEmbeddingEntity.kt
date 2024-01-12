package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey


@Entity(tableName = "user_embedding")
data class UserEmbeddingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "extraction_entity_id") val extractionEntityId: Long,
    val value: String
)

@Entity(tableName = "user_embedding_fts")
@Fts4(contentEntity = UserEmbeddingEntity::class)
data class UserEmbeddingFts(
    val value: String
)