package com.drbrosdev.extractor.domain.model

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