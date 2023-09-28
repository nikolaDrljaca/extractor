package com.drbrosdev.extractor.data.entity

import androidx.room.Entity
import com.drbrosdev.extractor.domain.usecase.LabelType


@Entity(
    tableName = "previous_search_entity",
    primaryKeys = ["query", "labelType"]
)
data class PreviousSearchEntity(
    val query: String,
    val resultCount: Int,
    val labelType: LabelType
)