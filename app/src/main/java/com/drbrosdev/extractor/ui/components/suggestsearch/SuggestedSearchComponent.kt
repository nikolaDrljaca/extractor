package com.drbrosdev.extractor.ui.components.suggestsearch

import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.SuggestedSearch
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.domain.usecase.suggestion.buildSuggestionScope
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.search.SearchNavTargetArgs
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SuggestedSearchComponent(
    private val coroutineScope: CoroutineScope,
    private val extractionStatus: Flow<ExtractionStatus>,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val navigators: Navigators
) {
    private val suggestedSearchScope = buildSuggestionScope {
        visual(amount = 4)
        text(amount = 4)
    }

    val state = extractionStatus
        .map {
            when (it) {
                is ExtractionStatus.Running -> SuggestedSearchState.Empty

                is ExtractionStatus.Done -> SuggestedSearchState.Content(
                    onSuggestionClick = { o -> onSearch(o) },
                    suggestions = compileSearchSuggestions.invoke(suggestedSearchScope)
                )
            }
        }
        .stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            SuggestedSearchState.Loading
        )

    private fun onSearch(suggestedSearch: SuggestedSearch) {
        val args = SearchNavTargetArgs(
            query = suggestedSearch.query,
            keywordType = suggestedSearch.keywordType,
            searchType = suggestedSearch.searchType,
            startRange = null,
            endRange = null
        )
        navigators.navController.navigate(ExtractorSearchNavTarget(args))
    }
}