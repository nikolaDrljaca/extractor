package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.usecase.image.search.SearchStrategy
import com.drbrosdev.extractor.framework.navigation.LocalDialogNavController
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGridState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.rememberExtractorSearchBottomSheetState
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorSearchNavTarget(
    private val query: String = "",
    private val keywordType: KeywordType = KeywordType.ALL
) : NavTarget {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ExtractorSearchViewModel = koinViewModel {
            parametersOf(query, keywordType)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()
        val sheetContent by viewModel.sheetContent.collectAsStateWithLifecycle()

        val navController = LocalNavController.current
        val dialogNavController = LocalDialogNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val context = LocalContext.current

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberExtractorSearchBottomSheetState()
        )

        val scope = rememberCoroutineScope()

        //Showcase the Search sheet on first launch
        LaunchedEffect(key1 = Unit) {
            val shouldShow = viewModel.shouldShowSheetFlow.first()
            if (shouldShow) {
                scaffoldState.bottomSheetState.expand()
                delay(1500L)
                scaffoldState.bottomSheetState.partialExpand()
                viewModel.onShowSheetDone()
            }
        }

        ExtractorSearchScreen(
            scaffoldState = scaffoldState,
            state = state,
            extractorStatusButtonState = viewModel.extractorStatusButtonState,
            searchViewState = viewModel.searchViewState,
            dateFilterState = viewModel.dateFilterState,
            loaderButtonState = viewModel.loaderButtonState,
            imageGridState = viewModel.gridState,
            sheetContent = sheetContent,
            snackbarHostState = viewModel.snackbarHostState,
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
            },
            onStatusButtonClick = {
                dialogNavController.navigate(ExtractorStatusDialogNavTarget)
            },
            onCreateAlbumClick = { viewModel.onCompileUserAlbum() },
            onSuggestedSearchClick = {
                viewModel.performSuggestedSearch(it)
            },
            onStartSyncClick = {
                viewModel.spawnWork()
            },
            onResetSearch = {
                viewModel.resetSearch()
                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
            },
            onMultiselectAction = {
                when (it) {
                    MultiselectAction.Cancel -> viewModel.onSelectionClear()
                    MultiselectAction.CreateAlbum -> viewModel.onSelectionCreate {
                        navController.navigate(ExtractorHomeNavTarget)
                    }

                    MultiselectAction.Share -> {
                        val uris = viewModel.getSelectedImageUris()
                        context.launchShareIntent(uris)
                    }

                    MultiselectAction.Delete -> Unit
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSearchScreen(
            onNavToDetail = {},
            onExtractorHomeClicked = {},
            onDone = {},
            onStatusButtonClick = {},
            onCreateAlbumClick = {},
            onSuggestedSearchClick = {},
            onResetSearch = {},
            onStartSyncClick = {},
            onMultiselectAction = {},
            extractorStatusButtonState = ExtractorStatusButtonState(),
            state = ExtractorSearchScreenUiState.StillIndexing,
            searchViewState = ExtractorSearchViewState("", KeywordType.ALL),
            dateFilterState = ExtractorDateFilterState(),
            loaderButtonState = ExtractorLoaderButtonState(),
            sheetContent = SheetContent.SearchView,
            snackbarHostState = SnackbarHostState(),
            imageGridState = ExtractorImageGridState(),
        )
    }
}
