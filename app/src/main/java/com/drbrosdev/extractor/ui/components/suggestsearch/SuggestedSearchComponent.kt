package com.drbrosdev.extractor.ui.components.suggestsearch

import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.domain.usecase.suggestion.buildSuggestionScope
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SuggestedSearchComponent(
    private val coroutineScope: CoroutineScope,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val onSearch: (SuggestedSearch) -> Unit
) {
    private val suggestedSearchScope = buildSuggestionScope {
        visual(amount = 4)
        text(amount = 4)
    }
    // state handler
    val state = flowOf(compileSearchSuggestions)
        .map { it.invoke(suggestedSearchScope) }
        .map {
            SuggestedSearchUiModel.Content(
                onSuggestionClick = ::onSearchClick,
                suggestions = it
            )
        }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            SuggestedSearchUiModel.Loading
        )
    // events
    fun onSearchClick(suggestedSearch: SuggestedSearch) = onSearch(suggestedSearch)
}