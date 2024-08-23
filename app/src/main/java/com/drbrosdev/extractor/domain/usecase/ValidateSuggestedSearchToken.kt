package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.domain.model.Token

private val stopWords = setOf(
    "a", "an", "and", "are", "as", "at", "be", "for", "from", "has",
    "he", "in", "is", "it", "its", "of", "on", "that", "the", "to",
    "was", "were", "will", "with", "by", "can", "do", "does", "doing",
    "done", "had", "has", "have", "having", "if", "into", "is", "it",
    "its", "not", "now", "or", "our", "should", "so", "than", "that", "the", "their", "there",
    "you", "your", "this", "com", "all", "ili", "die", "der"
)

fun Token.isValidSearchToken(): Boolean =
    when {
        text.length < 4 -> false
        text.toDoubleOrNull() != null -> false
        text in stopWords -> false
        else -> true
    }