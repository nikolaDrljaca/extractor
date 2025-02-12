package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetState
import com.drbrosdev.extractor.ui.components.searchsheet.rememberExtractorSearchBottomSheetState
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.purchase.ExtractorPurchaseSearchNavTarget
import com.drbrosdev.extractor.ui.home.ExtractorHomeNavTarget
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data object ExtractorSearchNavTarget : NavTarget {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorSearchViewModel = koinViewModel()
        val containerState by viewModel.containerState.collectAsStateWithLifecycle()
        val statusButtonState by viewModel.statusButtonState.collectAsStateWithLifecycle()
        val searchCount by viewModel.searchCount.collectAsStateWithLifecycle()

        val navController = navigators.navController
        val dialogNavController = navigators.dialogNavController
        val context = LocalContext.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberExtractorSearchBottomSheetState()
        )

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorSearchScreenEvents.AlbumCreated -> navController.navigate(
                    ExtractorHomeNavTarget
                )

                ExtractorSearchScreenEvents.NavToGetMore -> navController.navigate(
                    ExtractorPurchaseSearchNavTarget
                )

                is ExtractorSearchScreenEvents.NavToImage -> navController.navigate(
                    ExtractorImageViewerNavTarget(
                        images = it.imageUris,
                        initialIndex = it.initialIndex
                    )
                )

                ExtractorSearchScreenEvents.HideKeyboard -> {
                    keyboardController?.hide()
                }

                //Showcase the Search sheet on first launch
                ExtractorSearchScreenEvents.ShowSearchSheet -> {
                    scaffoldState.bottomSheetState.expand()
                    delay(1500L)
                    scaffoldState.bottomSheetState.partialExpand()
                    viewModel.onShowSheetDone()
                }

                is ExtractorSearchScreenEvents.ShareSelectedImages -> context.launchShareIntent(it.imageUris)
            }
        }

        ExtractorSearchScreen(
            scaffoldState = scaffoldState,
            state = containerState,
            searchCount = searchCount,
            extractorStatusButtonState = statusButtonState,
            searchSheetState = viewModel.searchSheetState,
            snackbarHostState = viewModel.snackbarHostState,
            onExtractorHomeClicked = { navController.navigate(ExtractorHomeNavTarget) },
            onStatusButtonClick = {
                dialogNavController.navigate(ExtractorStatusDialogNavTarget)
            },
            onMultiselectAction = viewModel::multiselectEventHandler,
            onHeaderClick = {
                navController.navigate(ExtractorPurchaseSearchNavTarget)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorSearchScreen(
            onExtractorHomeClicked = { },
            onStatusButtonClick = { },
            onMultiselectAction = { },
            onHeaderClick = { },
            state = ExtractorSearchContainerState.StillIndexing,
            searchCount = 21,
            extractorStatusButtonState = ExtractorStatusButtonState.Idle,
            snackbarHostState = SnackbarHostState(),
            searchSheetState = ExtractorSearchSheetState({}, SavedStateHandle())
        )
    }
}
