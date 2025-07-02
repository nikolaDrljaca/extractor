package com.drbrosdev.extractor.ui.search

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.SavedStateHandle
import arrow.core.left
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponent
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponentEvents
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorSearchNavTarget(val args: SearchNavTargetArgs? = null) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorSearchViewModel = koinViewModel {
            parametersOf(navigators)
        }

        val keyboardController = LocalSoftwareKeyboardController.current
        val context = LocalContext.current

        SideEffect {
            viewModel.onInitWithSearchArgs(args?.toSearchParams())
        }

        CollectFlow(viewModel.searchResultComponent.event) {
            when (it) {
                SearchResultComponentEvents.ScrollToTop -> {
                    viewModel.searchResultComponent
                        .gridState
                        .lazyGridState
                        .animateScrollToItem(0, scrollOffset = -1000)
                    viewModel.searchSheetComponent
                        .requestFocus()
                    keyboardController?.show()
                }

                SearchResultComponentEvents.SearchComplete ->
                    keyboardController?.hide()

                is SearchResultComponentEvents.Share -> context.launchShareIntent(it.images)
            }
        }

        LupaSearchScreen(
            sheetComponent = viewModel.searchSheetComponent,
            resultComponent = viewModel.searchResultComponent,
            snackbarHostState = viewModel.snackbarHostState,
            onBack = {
                keyboardController?.hide()
                navigators.navController.pop()
            }
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            LupaSearchScreen(
                sheetComponent = ExtractorSearchSheetComponent({}, SavedStateHandle()),
                resultComponent = SearchResultComponent(
                    coroutineScope = CoroutineScope(Dispatchers.Default),
                    searchImages = { Unit.left() },
                    createAlbum = {},
                    navigators = Navigators(
                        navController(emptyList()),
                        navController(emptyList()),
                        navController(emptyList()),
                    )
                ),
                snackbarHostState = SnackbarHostState(),
                onBack = {}
            )
        }
    }
}
