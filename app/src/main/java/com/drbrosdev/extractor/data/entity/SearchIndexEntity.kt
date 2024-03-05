package com.drbrosdev.extractor.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "search_index")
data class SearchIndexEntity(
    // indices
    val textIndex: String,
    val visualIndex: String,
    val userIndex: String,
    val colorIndex: String,
    // relation
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "extraction_entity_id") val extractionEntityId: Long
)

@Entity(tableName = "search_index_fts")
@Fts4(contentEntity = SearchIndexEntity::class)
data class SearchIndexFts(
    val textIndex: String,
    val visualIndex: String,
    val userIndex: String,
    val colorIndex: String,
)