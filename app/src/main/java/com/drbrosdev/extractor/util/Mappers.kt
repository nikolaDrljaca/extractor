package com.drbrosdev.extractor.util

import android.net.Uri
import androidx.core.net.toUri
import com.drbrosdev.extractor.data.album.record.AlbumConfigurationRecord
import com.drbrosdev.extractor.data.album.record.AlbumRecord
import com.drbrosdev.extractor.data.album.record.AlbumEntryRecord
import com.drbrosdev.extractor.data.extraction.record.ExtractionRecord
import com.drbrosdev.extractor.data.album.AlbumRelation
import com.drbrosdev.extractor.data.extraction.relation.ImageEmbeddingsRelation
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.AlbumPreview
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.MediaStoreImage
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum


fun MediaImageUri.toUri(): Uri = this.uri.toUri()

fun MediaStoreImage.mediaImageUri() = MediaImageUri(this.uri.toString())

fun MediaStoreImage.mediaImageId() = MediaImageId(this.mediaImageId)

fun MediaStoreImage.toExtraction() = Extraction(
    path = path,
    dateAdded = dateAdded,
    uri = mediaImageUri(),
    mediaImageId = mediaImageId()
)

fun ExtractionRecord.toExtraction() = Extraction(
    mediaImageId = MediaImageId(this.mediaStoreId),
    uri = MediaImageUri(this.uri),
    path = this.path,
    dateAdded = this.dateAdded
)

fun ImageEmbeddingsRelation.toImageEmbeds(): ImageEmbeds {
    val textEmbed = Embed.Text(value = this.textEmbeddingRecord.value)

    val visualEmbeds = when {
        this.visualEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.visualEmbeddingRecord.value
            .split(",")
            .map { Embed.Visual(it) }
    }

    val userEmbeds = when {
        this.userEmbeddingRecord.value.isBlank() -> emptyList()
        else -> this.userEmbeddingRecord.value
            .split(",")
            .map { Embed.User(it) }
    }

    return ImageEmbeds(
        textEmbed = textEmbed,
        visualEmbeds = visualEmbeds,
        userEmbeds = userEmbeds
    )
}

fun SearchType.toAlbumSearchType(): AlbumConfigurationRecord.SearchType = when (this) {
    SearchType.FULL -> AlbumConfigurationRecord.SearchType.FULL
    SearchType.PARTIAL -> AlbumConfigurationRecord.SearchType.PARTIAL
}

fun KeywordType.toAlbumLabelType(): AlbumConfigurationRecord.LabelType = when (this) {
    KeywordType.ALL -> AlbumConfigurationRecord.LabelType.ALL
    KeywordType.TEXT -> AlbumConfigurationRecord.LabelType.TEXT
    KeywordType.IMAGE -> AlbumConfigurationRecord.LabelType.IMAGE
}

fun NewAlbum.Origin.toAlbumOrigin(): AlbumRecord.Origin = when (this) {
    NewAlbum.Origin.VISUAL_COMPUTED -> AlbumRecord.Origin.VISUAL_COMPUTED
    NewAlbum.Origin.TEXT_COMPUTED -> AlbumRecord.Origin.TEXT_COMPUTED
    NewAlbum.Origin.USER_GENERATED -> AlbumRecord.Origin.USER_GENERATED
}

fun AlbumConfigurationRecord.LabelType.toLabelType(): KeywordType = when (this) {
    AlbumConfigurationRecord.LabelType.ALL -> KeywordType.ALL
    AlbumConfigurationRecord.LabelType.TEXT -> KeywordType.TEXT
    AlbumConfigurationRecord.LabelType.IMAGE -> KeywordType.IMAGE
}

fun AlbumConfigurationRecord.SearchType.toSearchType(): SearchType = when (this) {
    AlbumConfigurationRecord.SearchType.FULL -> SearchType.FULL
    AlbumConfigurationRecord.SearchType.PARTIAL -> SearchType.PARTIAL
}

fun AlbumEntryRecord.toAlbumEntry(): AlbumEntry {
    return AlbumEntry(
        uri = MediaImageUri(this.uri),
        id = MediaImageId(this.id)
    )
}

fun AlbumRelation.toAlbum(): Album {
    return Album(
        id = this.albumRecord.id,
        name = this.albumRecord.name,
        keyword = this.configuration.keyword,
        searchType = this.configuration.searchType.toSearchType(),
        keywordType = this.configuration.labelType.toLabelType(),
        entries = this.entries.map { it.toAlbumEntry() }
    )
}

fun Album.toPreview(): AlbumPreview {
    val thumbnail = when {
        entries.isNotEmpty() -> entries.first().uri
        else -> MediaImageUri(Uri.EMPTY.toString())
    }

    return AlbumPreview(
        id = this.id,
        name = this.name,
        thumbnail = thumbnail
    )
}