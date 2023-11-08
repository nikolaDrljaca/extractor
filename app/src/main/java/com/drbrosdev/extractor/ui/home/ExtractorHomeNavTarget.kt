package com.drbrosdev.extractor.ui.home

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalDialogNavController
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorHomeNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val extractorSearchViewModel: ExtractorSearchViewModel = koinViewModel()

        val topBarViewModel: ExtractorStatusButtonViewModel = koinViewModel()
        val extractorButtonState by topBarViewModel.state.collectAsStateWithLifecycle()

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
                    navController.navigate(
                        ExtractorSearchNavTarget(
                            query = it.query,
                            labelType = it.filter
                        )
                    )
                }
        }

        ExtractorHomeScreen(
            searchViewState = extractorSearchViewModel.state,
            extractorStatusButtonState = extractorButtonState,
            onSearchViewDone = { extractorSearchViewModel.performSearch() },
            onStatusButtonClick = { dialogNavController.navigate(ExtractorStatusDialogNavTarget) },
            onStatClick = { query, type ->
                navController.navigate(
                    ExtractorSearchNavTarget(
                        query = query,
                        labelType = type
                    )
                )
            },
            previousSearches = searches,
            onPreviousSearchEvents = {
                when (it) {
                    is PreviousSearchesEvents.OnDeleteSearch ->
                        previousSearchViewModel.deletePreviousSearch(it.previousSearch)

                    is PreviousSearchesEvents.PerformSearch -> {
                        keyboardController?.hide()
                        navController.navigate(
                            ExtractorSearchNavTarget(
                                query = it.query,
                                labelType = it.labelType
                            )
                        )
                    }
                }
            },
            statsUiState = statsUiState
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorHomeScreen(
                extractorStatusButtonState = ExtractorStatusButtonState.Idle,
                onStatusButtonClick = {},
                onSearchViewDone = {},
                onPreviousSearchEvents = {},
                onStatClick = { query, type -> },
                previousSearches = emptyList(),
                statsUiState = ExtractorStatsUiState.Loading,
                searchViewState = object : ExtractorSearchViewState {
                    override var query: String
                        get() = ""
                        set(value) {}

                    override var labelType: LabelType
                        get() = LabelType.ALL
                        set(value) {}

                }
            )
        }
    }
}
