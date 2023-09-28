package com.drbrosdev.extractor.ui.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewEvents
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarEvents
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarViewModel
import com.drbrosdev.extractor.ui.result.SearchResultNavTarget
import com.drbrosdev.extractor.ui.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.util.LocalDialogNavController
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object HomeNavTarget : NavTarget {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val extractorSearchViewModel: ExtractorSearchViewModel = koinViewModel()

        val topBarViewModel: ExtractorTopBarViewModel = koinViewModel()
        val donePercentage by topBarViewModel.percentageDoneFlow.collectAsState()

        val previousSearchViewModel: PreviousSearchesViewModel = koinViewModel()
        val searches by previousSearchViewModel.prevSearchesFlow.collectAsState()

        val navController = LocalNavController.current
        val dialogNavController = LocalDialogNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(key1 = Unit) {
            extractorSearchViewModel.events
                .collect {
                    navController.navigate(
                        SearchResultNavTarget(
                            query = it.query,
                            labelType = it.filter
                        )
                    )
                }
        }

        HomeScreen(
            donePercentage = donePercentage,
            onTopBarEvent = {
                when (it) {
                    ExtractorTopBarEvents.OnAboutClicked -> {}
                    ExtractorTopBarEvents.OnExtractorButtonClicked -> {
                        dialogNavController.navigate(ExtractorStatusDialogNavTarget)
                    }
                }
            },
            onSearchViewEvents = {
                when (it) {
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
                when (it) {
                    is PreviousSearchesEvents.OnDeleteSearch ->
                        previousSearchViewModel.deletePreviousSearch(it.previousSearch)

                    is PreviousSearchesEvents.PerformSearch -> {
                        keyboardController?.hide()
                        navController.navigate(
                            SearchResultNavTarget(
                                query = it.query,
                                labelType = it.labelType
                            )
                        )
                    }
                }
            }
        )
    }
}