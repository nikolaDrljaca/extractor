package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultRunExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val createMediaImageData: CreateMediaImageData,
    private val extractTextEmbed: ExtractTextEmbed,
    private val extractVisualEmbeds: ExtractVisualEmbeds,
) : RunExtractor {

    override suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds? {
        return withContext(dispatcher) {
            // create media image data - prepare for extraction
            val data = createMediaImageData.execute(mediaImageUri) ?: return@withContext null
            // extract text data
            val text = async {
                extractTextEmbed.execute(data)
            }
            // extract visual data
            val visuals = async {
                extractVisualEmbeds.execute(data)
            }
            // return embeds
            ImageEmbeds(
                textEmbed = text.await(),
                visualEmbeds = visuals.await(),
                userEmbeds = emptyList()
            )
        }
    }
}
