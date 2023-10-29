package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


data class ImageDataWithEmbeddings(
    @Embedded val imageEntity: ExtractionEntity,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "image_entity_id"
    )
    val textEmbedding: TextEmbedding,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "image_entity_id"
    )
    val visualEmbeddings: List<VisualEmbedding>,

    @Relation(
        parentColumn = "media_store_id",
        entityColumn = "image_entity_id"
    )
    val userEmbedding: UserEmbedding?,
)

@Entity(tableName = "text_embedding")
data class TextEmbedding(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "image_entity_id") val imageEntityId: Long,
    val value: String
)

/**
 * Visual embeddings are also unique by <code>value</code>.
 */
@Entity(tableName = "visual_embedding")
data class VisualEmbedding(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "image_entity_id") val imageEntityId: Long,
    val value: String
)

@Entity(tableName = "user_embedding")
data class UserEmbedding(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "image_entity_id") val imageEntityId: Long,
    val value: String
)