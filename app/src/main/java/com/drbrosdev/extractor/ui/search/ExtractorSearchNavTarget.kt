package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.usecase.image.search.SearchStrategy
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonViewModel
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorSearchNavTarget(
    private val query: String = "",
    private val labelType: LabelType = LabelType.ALL
) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorSearchViewModel = koinViewModel {
            parametersOf(query, labelType)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()

        val extractorStatusButtonViewModel: ExtractorStatusButtonViewModel = koinViewModel()
        val extractorStatusButtonState by extractorStatusButtonViewModel.state.collectAsStateWithLifecycle()

        val navController = LocalNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        //TODO: Loading state, animated placeholders or spinners
        ExtractorSearchScreen(
            state = state,
            extractorStatusButtonState = extractorStatusButtonState,
            searchViewState = viewModel.searchViewState,
            dateFilterState = viewModel.dateFilterState,
            onNavToDetail = { selectedIndex ->
                navController.navigate(
                    ExtractorImageNavTarget(
                        images = viewModel.getImageUris(),
                        initialIndex = selectedIndex
                    )
                )
            },
            onExtractorHomeClicked = { navController.navigate(ExtractorHomeNavTarget) },
            onDone = {
                keyboardController?.hide()
                viewModel.performSearch(SearchStrategy.DIRTY_CHECKING)
            }
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSearchScreen(
            state = ExtractorSearchScreenUiState.Loading,
            onNavToDetail = {},
            extractorStatusButtonState = ExtractorStatusButtonState.Idle,
            onExtractorHomeClicked = {},
            onDone = {},
            searchViewState = ExtractorSearchViewState("", LabelType.ALL),
            dateFilterState = ExtractorDateFilterState(),
        )
    }
}
