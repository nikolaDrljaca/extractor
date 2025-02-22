package com.drbrosdev.extractor.domain.usecase.token

import com.drbrosdev.extractor.domain.model.Token

class GenerateMostCommonTokens {
    operator fun invoke(tokens: Collection<Token>, amount: Int = 7) =
        tokens.groupingBy { it.text.lowercase() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { Token(it.key) }
            .take(amount)
}