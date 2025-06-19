package com.drbrosdev.extractor.domain.usecase.generate

import com.drbrosdev.extractor.domain.model.LupaBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GenerateMostCommonExtractionBundles(
    private val compileMostCommonVisualEmbeds: CompileMostCommonVisualEmbeds,
    private val compileMostCommonTextEmbeds: CompileMostCommonTextEmbeds
) {
    suspend fun execute(): List<LupaBundle> = withContext(Dispatchers.Default) {
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
    private fun List<LupaBundle>.trim(): List<LupaBundle> {
        return this.map {
            it.copy(images = it.images.take(n = 20))
        }
    }
}