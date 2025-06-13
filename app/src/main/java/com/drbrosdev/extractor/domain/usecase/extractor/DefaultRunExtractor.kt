package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.service.InferenceService

class DefaultRunExtractor(
    private val inferenceService: InferenceService,
    /*
    private val createMediaImageData: CreateMediaImageData,
    private val extractTextEmbed: ExtractTextEmbed,
    private val extractVisualEmbeds: ExtractVisualEmbeds,
     */
) : RunExtractor {

    // TODO: @drljacan replace all extractEmbed* methods (remove them) and depend on the inference service
    // TODO: Too many layers of indirection - not necessary - they are not common (reused) logic
    // TODO: Handle non-cancellation errors here
    // TODO: Remove redundant interface
    override suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds? {
        // NOTE: inferenceService calls are main-safe
        // create media image data - prepare for extraction

        // extract text data - async

        // extract visual data - async

        // return embeds
        return ImageEmbeds(
            textEmbed = Embed.Text(""),
            visualEmbeds = emptyList(),
            userEmbeds = emptyList()
        )
    }
}
