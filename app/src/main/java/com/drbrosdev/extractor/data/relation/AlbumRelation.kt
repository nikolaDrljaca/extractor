package com.drbrosdev.extractor.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.drbrosdev.extractor.data.entity.AlbumConfigurationEntity
import com.drbrosdev.extractor.data.entity.AlbumEntity
import com.drbrosdev.extractor.data.entity.AlbumEntryEntity


data class AlbumRelation(
    @Embedded
    val albumEntity: AlbumEntity,

    @Relation(
        parentColumn = "album_id",
        entityColumn = "album_entity_id"
    )
    val configuration: AlbumConfigurationEntity,

    @Relation(
        parentColumn = "album_id",
        entityColumn = "album_entity_id"
    )
    val entries: List<AlbumEntryEntity>
)
