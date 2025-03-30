package com.drbrosdev.extractor.domain.usecase.generate

import com.drbrosdev.extractor.domain.model.ExtractionBundle
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GenerateMostCommonExtractionBundles(
    private val compileMostCommonVisualEmbeds: CompileMostCommonVisualEmbeds,
    private val compileMostCommonTextEmbeds: CompileMostCommonTextEmbeds
) {
    suspend fun execute(): List<ExtractionBundle> = coroutineScope {
        val textContent = async { compileMostCommonTextEmbeds.execute(4) }
        val visualContent = async { compileMostCommonVisualEmbeds.execute(4) }
        textContent.await()
            .plus(visualContent.await())
            .shuffled()
    }
}