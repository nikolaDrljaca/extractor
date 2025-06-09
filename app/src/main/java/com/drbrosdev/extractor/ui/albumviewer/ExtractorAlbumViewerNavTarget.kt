package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorAlbumViewerNavTarget(
    private val albumId: Long
) : NavTarget {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorAlbumViewerViewModel = koinViewModel {
            parametersOf(albumId, navigators)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()
        val dialogSelectionState by viewModel.dialogSelectionState.collectAsStateWithLifecycle()

        val snackbarHostState = remember {
            SnackbarHostState()
        }

        val navController = navigators.navController
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        CollectFlow(viewModel.events) {
            when (it) {
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

                is ExtractorAlbumViewerEvents.ShareAlbumEntries -> {
                    context.launchShareIntent(it.uris)
                }
            }
        }

        when {
            state != null -> {
                ExtractorAlbumViewerScreen(
                    state = state!!,
                    imageGridState = viewModel.gridState,
                    snackbarHostState = snackbarHostState,
                    dialogSelectionState = dialogSelectionState,
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
                                viewModel.onShareConfirmed()
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

            else -> {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadingIndicator()
                }
            }
        }
    }
}


@Composable
@ScreenPreview
private fun CurrentPreview() {
    val state = ExtractorAlbumViewerState(
        id = 1L,
        hero = AlbumHeroUiModel(
            name = "Foobar",
            description = "image * partial * 21",
            heroImage = MediaImageUri(Uri.EMPTY.toString())
        ),
        entries = emptyList(),
        shouldShowSelectBar = false,
        eventSink = {}
    )
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorAlbumViewerScreen(
                state = state,
                imageGridState = ExtractorGridState(),
                snackbarHostState = SnackbarHostState(),
                dialogSelectionState = ExtractorAlbumDialogSelection.None,
                onDeleteDialogAction = {
                },
                onShareDialogAction = {
                },
                onMultiselectAction = {
                }
            )
        }
    }
}
