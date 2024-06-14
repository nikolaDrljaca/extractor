package com.drbrosdev.extractor.ui.albumviewer

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.ExtractorAlbumBottomSheetAction
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorAlbumViewerNavTarget(
    private val albumId: Long
) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorAlbumViewerViewModel = koinViewModel {
            parametersOf(albumId)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarHostState = remember {
            SnackbarHostState()
        }

        val navController = navigators.navController
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        CollectFlow(viewModel.events) {
            when (it) {
                ExtractorAlbumViewerEvents.SelectionCreated -> {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.snack_album_created),
                            actionLabel = context.getString(R.string.snack_view)
                        )
                        when (result) {
                            SnackbarResult.Dismissed -> Unit
                            SnackbarResult.ActionPerformed -> navController.pop()
                        }
                    }
                }

                ExtractorAlbumViewerEvents.AlbumDeleted -> {
                    navController.pop()
                }

                ExtractorAlbumViewerEvents.SelectionDeleted -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.items_deleted),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                ExtractorAlbumViewerEvents.SelectionShared -> {
                    context.launchShareIntent(viewModel.imageUris.value)
                }
            }
        }

        ExtractorAlbumViewerScreen(
            onImageClick = { index ->
                val destination = ExtractorImageViewerNavTarget(
                    images = viewModel.imageUris.value,
                    initialIndex = index
                )
                navController.navigate(destination)
            },
            state = state,
            imageGridState = viewModel.gridState,
            snackbarHostState = snackbarHostState,
            onBack = { navController.pop() },
            onFabClick = viewModel::onShowBottomSheet,
            onBottomSheetAction = {
                when (it) {
                    ExtractorAlbumBottomSheetAction.Delete -> viewModel.onBottomSheetDelete()
                    ExtractorAlbumBottomSheetAction.Dismiss -> viewModel.onDismissDialog()
                    ExtractorAlbumBottomSheetAction.Share -> viewModel.onShareAction()
                }
            },
            onDeleteDialogAction = {
                when (it) {
                    ConfirmationDialogActions.Confirm -> {
                        viewModel.onDeleteAlbum()
                        navController.pop()
                    }

                    ConfirmationDialogActions.Deny -> viewModel.onDismissDialog()
                    ConfirmationDialogActions.Dismiss -> viewModel.onDismissDialog()
                }
            },
            onShareDialogAction = {
                when (it) {
                    ConfirmationDialogActions.Confirm -> {
                        context.launchShareIntent(viewModel.imageUris.value)
                        viewModel.onDismissDialog()
                    }

                    ConfirmationDialogActions.Deny -> viewModel.onDismissDialog()
                    ConfirmationDialogActions.Dismiss -> viewModel.onDismissDialog()
                }
            },
            onMultiselectAction = {
                when (it) {
                    MultiselectAction.Cancel -> viewModel.onSelectionClear()
                    MultiselectAction.CreateAlbum -> viewModel.onSelectionCreate()

                    MultiselectAction.Share -> {
                        val uris = viewModel.getSelectedUris()
                        context.launchShareIntent(uris)
                    }

                    MultiselectAction.Delete -> viewModel.onDeleteSelection()
                }
            }
        )
    }
}


@Composable
@ScreenPreview
private fun CurrentPreview() {
    val data = ExtractorAlbumViewerScreenState.Content(
        album = Album(
            id = 0L,
            name = "Some album",
            keyword = "keyword",
            searchType = SearchType.PARTIAL,
            keywordType = KeywordType.IMAGE,
            entries = listOf(
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(11L)),
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(12L)),
                AlbumEntry(uri = MediaImageUri(""), id = MediaImageId(13L)),
            )
        ),
        dialogSelection = ExtractorAlbumDialogSelection.None
    )
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorAlbumViewerScreen(
                state = data,
                imageGridState = ExtractorGridState(),
                snackbarHostState = SnackbarHostState(),
                onImageClick = {},
                onBack = {},
                onDeleteDialogAction = {},
                onShareDialogAction = {},
                onMultiselectAction = {},
                onBottomSheetAction = {},
                onFabClick = {}
            )
        }
    }
}
