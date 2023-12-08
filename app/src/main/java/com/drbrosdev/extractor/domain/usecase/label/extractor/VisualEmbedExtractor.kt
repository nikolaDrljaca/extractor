package com.drbrosdev.extractor.domain.usecase.label.extractor

import com.drbrosdev.extractor.domain.model.Embed


interface VisualEmbedExtractor<T> {

    suspend fun execute(image: T): Result<List<Embed.Visual>>
}
