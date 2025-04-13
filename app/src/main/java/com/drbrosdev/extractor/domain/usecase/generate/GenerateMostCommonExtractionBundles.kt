package com.drbrosdev.extractor.domain.usecase.generate

import com.drbrosdev.extractor.domain.model.ExtractionBundle
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GenerateMostCommonExtractionBundles(
    private val compileMostCommonVisualEmbeds: CompileMostCommonVisualEmbeds,
    private val compileMostCommonTextEmbeds: CompileMostCommonTextEmbeds
) {
    suspend fun execute(): List<ExtractionBundle> = coroutineScope {
        val textContent = async {
            compileMostCommonTextEmbeds.execute(4)
                .trim()
        }
        val visualContent = async {
            compileMostCommonVisualEmbeds.execute(4)
                .trim()
        }

        textContent.await()
            .plus(visualContent.await())
            .shuffled()
    }

    // limit each bundle to at most 20 images
    private fun List<ExtractionBundle>.trim(): List<ExtractionBundle> {
        return this.map {
            it.copy(extractions = it.extractions.take(n = 20))
        }
    }
}