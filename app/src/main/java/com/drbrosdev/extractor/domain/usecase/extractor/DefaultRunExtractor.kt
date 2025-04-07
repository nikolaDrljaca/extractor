package com.drbrosdev.extractor.domain.usecase.extractor

import com.drbrosdev.extractor.domain.model.ImageEmbeds
import com.drbrosdev.extractor.domain.model.MediaImageUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DefaultRunExtractor(
    private val dispatcher: CoroutineDispatcher,
    private val extractTextEmbed: ExtractTextEmbed,
    private val extractVisualEmbeds: ExtractVisualEmbeds,
) : RunExtractor {

    override suspend fun execute(mediaImageUri: MediaImageUri): ImageEmbeds {
        return withContext(dispatcher) {
            val text = async {
                extractTextEmbed.execute(mediaImageUri)
            }

            val visuals = async {
                extractVisualEmbeds.execute(mediaImageUri)
            }

            val outText = text.await()

            val outVisualEmbeds = visuals.await()
                .distinctBy { it.value.lowercase() }
                .toList()

            ImageEmbeds(
                textEmbed = outText,
                visualEmbeds = outVisualEmbeds,
                userEmbeds = emptyList()
            )
        }
    }
}
