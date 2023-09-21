package com.drbrosdev.extractor.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewEvents
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarViewModel
import com.drbrosdev.extractor.ui.result.SearchResultNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object HomeNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val extractorSearchViewModel: ExtractorSearchViewModel = koinViewModel()

        val topBarViewModel: ExtractorTopBarViewModel = koinViewModel()
        val donePercentage by topBarViewModel.percentageDoneFlow.collectAsState()

        val previousSearchViewModel: PreviousSearchesViewModel = koinViewModel()
        val searches by previousSearchViewModel.prevSearchesFlow.collectAsState()

        val navController = LocalNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(key1 = Unit) {
            extractorSearchViewModel.events
                .collect {
                    navController.navigate(
                        SearchResultNavTarget(
                            it.query
                        )
                    )
                }
        }

        HomeScreen(
            donePercentage = donePercentage,
            onTopBarEvent = {},
            onSearchViewEvents = {
                when(it) {
                    is ExtractorSearchViewEvents.OnImageLabelFilterChanged ->
                        extractorSearchViewModel.onFilterChanged(it.data)
                    is ExtractorSearchViewEvents.OnPerformSearch ->
                        extractorSearchViewModel.performSearch()
                    is ExtractorSearchViewEvents.OnQueryChanged ->
                        extractorSearchViewModel.onQueryChanged(it.data)
                }
            },
            previousSearches = searches,
            onPreviousSearchEvents = {
                when(it) {
                    is PreviousSearchesEvents.OnDeleteSearch ->
                        previousSearchViewModel.deletePreviousSearch(it.previousSearch)
                    is PreviousSearchesEvents.PerformSearch -> {
                        keyboardController?.hide()
                    }
                }
            }
        )
    }
}