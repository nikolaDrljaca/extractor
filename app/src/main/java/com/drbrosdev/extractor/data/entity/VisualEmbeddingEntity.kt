package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



/**
 * Visual embeddings are also unique by <code>value</code>.
 */
@Entity(tableName = "visual_embedding")
data class VisualEmbeddingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "image_entity_id") val imageEntityId: Long,
    val value: String
)
