package com.drbrosdev.extractor.domain.model

import java.time.LocalDateTime

// TODO Rename to LupaAnnotations
// LupaImage is current extraction and should be extended to contain annotations
// to properly represent it as a domain model
// LupaImage.Annotations - ImageEmbeds - Or Just lupaAnnotations
// OR
/*
LupaImage = Metadata + Annotatinos
LupaMetadata - current extraction
LupaAnnoations - current ImageEmbeds

RunExtractor - ExtractLupaAnnotations
BulkExtractLupaAnnotations - RunBulkExtractor
Extraction as a process can remain - Progress/Status etc

Bundles become LupaBundles

Search related models:
Keyword search type
ImageSearchParams
FtsQuery
SuggestedSearch
DateRange

Album related:
album + category + entry

Others just in root folder

Domain types annotated with compose related @ should become UI models and stored under UI package
 */
data class ImageEmbeds(
    val textEmbed: Embed.Text,
    val visualEmbeds: List<Embed.Visual>,
    val userEmbeds: List<Embed.User>
)

// Extraction is this
data class LupaImageMetadata(
    val mediaImageId: MediaImageId,
    val uri: MediaImageUri,
    val path: String,
    val dateAdded: LocalDateTime,
)

// ImageEmbeds is this
data class LupaImageAnnotations(
    val textEmbed: String,
    val visualEmbeds: List<String>,
    val userEmbeds: List<String>
)

// ExtractionData can be this
data class LupaImage(
    val metadata: LupaImageMetadata,
    val annotations: LupaImageAnnotations
)