package com.drbrosdev.extractor.domain.usecase.text.extractor

import com.drbrosdev.extractor.domain.model.Embed

interface ExtractTextEmbed<T> {

    suspend fun execute(image: T): Result<Embed.Text>

}
