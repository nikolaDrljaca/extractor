package com.drbrosdev.extractor.data.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "search_index_fts")
@Fts4(contentEntity = SearchIndexRecord::class)
data class SearchIndexFts(
    @ColumnInfo(name = "text_index")
    val textIndex: String,

    @ColumnInfo(name = "visual_index")
    val visualIndex: String,

    @ColumnInfo(name = "user_index")
    val userIndex: String,
)