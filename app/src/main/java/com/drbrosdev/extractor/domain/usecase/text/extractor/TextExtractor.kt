package com.drbrosdev.extractor.domain.usecase.text.extractor

interface TextExtractor<T> {
    suspend fun execute(image: T): Result<String>
}
