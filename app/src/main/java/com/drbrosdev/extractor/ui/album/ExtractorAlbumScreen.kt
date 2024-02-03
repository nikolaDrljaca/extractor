package com.drbrosdev.extractor.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGridState
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialog
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogDefaults
import com.drbrosdev.extractor.ui.components.shared.ExtractorAlbumActionBottomSheet
import com.drbrosdev.extractor.ui.components.shared.ExtractorAlbumBottomSheetAction
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction

@Composable
fun ExtractorAlbumScreen(
    modifier: Modifier = Modifier,
    onImageClick: (index: Int) -> Unit,
    onDeleteDialogAction: (ConfirmationDialogActions) -> Unit,
    onShareDialogAction: (ConfirmationDialogActions) -> Unit,
    onBottomSheetAction: (ExtractorAlbumBottomSheetAction) -> Unit,
    onBack: () -> Unit,
    onFabClick: () -> Unit,
    onMultiselectAction: (MultiselectAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    state: ExtractorAlbumScreenState,
    imageGridState: ExtractorImageGridState
) {
    val extractorTopBarState = remember {
        derivedStateOf {
            if (imageGridState.lazyGridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    when (state) {
        ExtractorAlbumScreenState.Loading -> ExtractorAlbumScreenLoading()
        is ExtractorAlbumScreenState.Content -> {
            when (state.dialogSelection) {
                ExtractorAlbumDialogSelection.BottomSheet -> {
                    ExtractorAlbumActionBottomSheet(onAction = onBottomSheetAction)
                }

                ExtractorAlbumDialogSelection.ConfirmDelete -> {
                    ConfirmationDialog(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "",
                                modifier = Modifier.size(ConfirmationDialogDefaults.iconSize.dp)
                            )
                        },
                        onAction = onDeleteDialogAction
                    ) {
                        Text(text = stringResource(R.string.album_delete_perm))
                    }
                }

                ExtractorAlbumDialogSelection.ConfirmShare -> {
                    ConfirmationDialog(
                        onAction = onShareDialogAction
                    ) {
                        Text(
                            text = stringResource(
                                R.string.too_many_images,
                                state.album.entries.size
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                ExtractorAlbumDialogSelection.None -> Unit
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ExtractorImageGrid(
                    onClick = {
                        onImageClick(it)
                    },
                    albumEntries = state.album.entries,
                    state = imageGridState
                )

                ExtractorTopBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = extractorTopBarState.value,
                    centerSlot = {
                        AlbumHeader(
                            onBack = onBack,
                            keyword = state.album.keyword,
                            metadata = state.metadata
                        )
                    }
                )

                AnimatedVisibility(
                    visible = state.shouldShowSelectBar,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ExtractorMultiselectActionBar(onAction = onMultiselectAction)
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 64.dp + bottomPadding),
                    snackbar = { ExtractorSnackbar(snackbarData = it) }
                )

                AnimatedVisibility(
                    visible = !state.shouldShowSelectBar,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 32.dp + bottomPadding, end = 32.dp)
                ) {
                    FloatingActionButton(
                        onClick = onFabClick,
                    ) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtractorAlbumScreenLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun AlbumHeader(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    keyword: String,
    metadata: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackIconButton(onBack = onBack)
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "# $keyword",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = metadata,
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
            )
        }
    }
}