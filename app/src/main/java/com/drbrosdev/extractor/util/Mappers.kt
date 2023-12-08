package com.drbrosdev.extractor.util

import android.net.Uri
import com.drbrosdev.extractor.data.entity.ExtractionEntity
import com.drbrosdev.extractor.data.relation.ImageEmbeddingsRelation
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.framework.mediastore.MediaStoreImage


fun MediaImageUri.toUri(): Uri = Uri.parse(Uri.decode(this.uri))

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
