package com.drbrosdev.extractor.util

inline fun <reified I, O> memo(crossinline fn: (I) -> O): (I) -> O {
    val computations = mutableMapOf<I, O>()
    return { input ->
        when {
            computations.containsKey(input) -> computations[input]!!

            else -> {
                val result = fn(input)
                computations[input] = result
                result
            }
        }
    }
}

inline fun <O> memo(
    tag: String,
    crossinline fn: () -> O
): () -> O {
    val computations = mutableMapOf<String, O>()
    return {
        when {
            computations.containsKey(tag) -> computations[tag]!!

            else -> {
                val result = fn()
                computations[tag] = result
                result
            }
        }
    }
}
