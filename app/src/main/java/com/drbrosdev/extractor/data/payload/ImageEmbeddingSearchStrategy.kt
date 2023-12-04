package com.drbrosdev.extractor.data.payload

sealed class ImageEmbeddingSearchStrategy {
    abstract val query: String

    data class Full(
        override val query: String
    ) : ImageEmbeddingSearchStrategy()

    class Partial(private val initQuery: String) : ImageEmbeddingSearchStrategy() {
        override val query: String
            get() = "%$initQuery%"
    }
}