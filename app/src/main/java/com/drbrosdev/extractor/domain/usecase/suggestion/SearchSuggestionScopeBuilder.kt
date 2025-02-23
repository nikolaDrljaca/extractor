package com.drbrosdev.extractor.domain.usecase.suggestion

import com.drbrosdev.extractor.domain.model.KeywordType

class SearchSuggestionScopeBuilder {
    private val scopes = mutableListOf<SearchSuggestionScope>()

    fun visual(amount: Int = SearchSuggestionScope.TAKE_VISUAL) =
        scopes.add(SearchSuggestionScope(amount, KeywordType.IMAGE))

    fun text(amount: Int = SearchSuggestionScope.TAKE_TEXT) =
        scopes.add(SearchSuggestionScope(amount, KeywordType.TEXT))

    fun user(amount: Int = SearchSuggestionScope.TAKE_USER) =
        scopes.add(SearchSuggestionScope(amount, KeywordType.ALL))

    fun scope(amount: Int, type: KeywordType) = scopes.add(SearchSuggestionScope(amount, type))

    fun build(): List<SearchSuggestionScope> = scopes
}

fun buildSuggestionScope(block: SearchSuggestionScopeBuilder.() -> Unit): List<SearchSuggestionScope> {
    return SearchSuggestionScopeBuilder().apply(block).build()
}
