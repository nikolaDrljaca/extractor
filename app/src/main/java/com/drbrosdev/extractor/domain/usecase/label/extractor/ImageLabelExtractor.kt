package com.drbrosdev.extractor.domain.usecase.label.extractor


interface ImageLabelExtractor<T> {
    suspend fun execute(image: T): Result<List<String>>
}
