package com.drbrosdev.extractor.domain.usecase.extractor.visual

import com.drbrosdev.extractor.domain.model.Embed


interface ExtractVisualEmbeds<T> {

    suspend fun execute(image: T): List<Embed.Visual>

}
