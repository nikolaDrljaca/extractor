package com.drbrosdev.extractor.ui.home

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalDialogNavController
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.navigateToSearchScreen
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorHomeNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val extractorSearchViewModel: ExtractorSearchViewModel = koinViewModel()

        val previousSearchViewModel: PreviousSearchesViewModel = koinViewModel()
        val searches by previousSearchViewModel.prevSearchesFlow.collectAsStateWithLifecycle()

        val statsViewModel: ExtractorStatsViewModel = koinViewModel()
        val statsUiState by statsViewModel.state.collectAsStateWithLifecycle()

        val navController = LocalNavController.current
        val dialogNavController = LocalDialogNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(key1 = Unit) {
            extractorSearchViewModel.events
                .collect {
                    navController.navigateToSearchScreen(
                        query = it.query,
                        labelType = it.filter
                    )
                }
        }

        ExtractorHomeScreen(
            statsUiState = statsUiState,
            previousSearches = searches,
            onSyncClick = { dialogNavController.navigate(ExtractorStatusDialogNavTarget) },
            onStatClick = { query, type ->
                navController.navigateToSearchScreen(
                    query = query,
                    labelType = type
                )
            },
            onPreviousSearchEvents = {
                when (it) {
                    is PreviousSearchesEvents.OnDeleteSearch ->
                        previousSearchViewModel.deletePreviousSearch(it.previousSearch)

                    is PreviousSearchesEvents.PerformSearch -> {
                        keyboardController?.hide()
                        navController.navigateToSearchScreen(
                            query = it.query,
                            labelType = it.labelType
                        )
                    }
                }
            },
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorHomeScreen(
                onPreviousSearchEvents = {},
                onStatClick = { query, type -> },
                onSyncClick = {},
                previousSearches = emptyList(),
                statsUiState = ExtractorStatsUiState.Loading,
            )
        }
    }
}
