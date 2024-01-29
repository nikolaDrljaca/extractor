package com.drbrosdev.extractor.util

import android.net.Uri
import androidx.core.net.toUri
import com.drbrosdev.extractor.data.entity.AlbumConfigurationEntity
import com.drbrosdev.extractor.data.entity.AlbumEntity
import com.drbrosdev.extractor.data.entity.AlbumEntryEntity
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.relation.AlbumRelation
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.AlbumPreview
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.model.MediaStoreImage


fun MediaImageUri.toUri(): Uri = this.uri.toUri()

fun MediaStoreImage.mediaImageUri() = MediaImageUri(this.uri.toString())

fun MediaStoreImage.mediaImageId() = MediaImageId(this.mediaImageId)

fun MediaStoreImage.toExtraction() = Extraction(
    path = path,
    dateAdded = dateAdded,
    uri = mediaImageUri(),
    mediaImageId = mediaImageId()
)

fun ExtractionEntity.toExtraction() = Extraction(
    mediaImageId = MediaImageId(this.mediaStoreId),
    uri = MediaImageUri(this.uri),
    path = this.path,
    dateAdded = this.dateAdded
)

fun ImageEmbeddingsRelation.mapToImageEmbeds(): ImageEmbeds {
    val textEmbed = Embed.Text(value = this.textEmbeddingEntity.value)
    val userEmbed = this.userEmbeddingEntity?.let {
        Embed.User(it.value)
    }
    val visualEmbeds = this.visualEmbeddingEntities.map {
        Embed.Visual(it.value)
    }

    return ImageEmbeds(
        textEmbed = textEmbed,
        visualEmbeds = visualEmbeds,
        userEmbeds = userEmbed
    )
}

fun SearchType.toAlbumSearchType(): AlbumConfigurationEntity.SearchType = when (this) {
    SearchType.FULL -> AlbumConfigurationEntity.SearchType.FULL
    SearchType.PARTIAL -> AlbumConfigurationEntity.SearchType.PARTIAL
}

fun KeywordType.toAlbumLabelType(): AlbumConfigurationEntity.LabelType = when (this) {
    KeywordType.ALL -> AlbumConfigurationEntity.LabelType.ALL
    KeywordType.TEXT -> AlbumConfigurationEntity.LabelType.TEXT
    KeywordType.IMAGE -> AlbumConfigurationEntity.LabelType.IMAGE
}

fun NewAlbum.Origin.toAlbumOrigin(): AlbumEntity.Origin = when (this) {
    NewAlbum.Origin.VISUAL_COMPUTED -> AlbumEntity.Origin.VISUAL_COMPUTED
    NewAlbum.Origin.TEXT_COMPUTED -> AlbumEntity.Origin.TEXT_COMPUTED
    NewAlbum.Origin.USER_GENERATED -> AlbumEntity.Origin.USER_GENERATED
}

fun AlbumConfigurationEntity.LabelType.toLabelType(): KeywordType = when (this) {
    AlbumConfigurationEntity.LabelType.ALL -> KeywordType.ALL
    AlbumConfigurationEntity.LabelType.TEXT -> KeywordType.TEXT
    AlbumConfigurationEntity.LabelType.IMAGE -> KeywordType.IMAGE
}

fun AlbumConfigurationEntity.SearchType.toSearchType(): SearchType = when (this) {
    AlbumConfigurationEntity.SearchType.FULL -> SearchType.FULL
    AlbumConfigurationEntity.SearchType.PARTIAL -> SearchType.PARTIAL
}

fun AlbumEntryEntity.toAlbumEntry(): AlbumEntry {
    return AlbumEntry(
        uri = MediaImageUri(this.uri),
        id = MediaImageId(this.id)
    )
}

fun AlbumRelation.toAlbum(): Album {
    return Album(
        id = this.albumEntity.id,
        name = this.albumEntity.name,
        keyword = this.configuration.keyword,
        searchType = this.configuration.searchType.toSearchType(),
        keywordType = this.configuration.labelType.toLabelType(),
        entries = this.entries.map { it.toAlbumEntry() }
    )
}

fun Album.toPreview(): AlbumPreview {
    return AlbumPreview(
        id = this.id,
        name = this.name,
        thumbnail = this.entries.first().uri
    )
}