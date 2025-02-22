package com.drbrosdev.extractor.domain.usecase.extractor.text

import com.drbrosdev.extractor.domain.model.Embed

interface ExtractTextEmbed<T> {

    suspend fun execute(image: T): Embed.Text

}
