package com.drbrosdev.extractor.ui.albumviewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialog
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogDefaults
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.shared.albumGridActions
import com.drbrosdev.extractor.util.maxLineSpanItem

@Stable
data class ExtractorAlbumViewerState(
    val id: Long,
    val hero: AlbumHeroUiModel,
    val entries: List<AlbumEntry>,
    val shouldShowSelectBar: Boolean,
    val eventSink: (AlbumViewerEvents) -> Unit
)

sealed interface AlbumViewerEvents {
    data object Delete : AlbumViewerEvents

    data object Share : AlbumViewerEvents


    data object GoBack : AlbumViewerEvents

    data class GoToImageViewer(
        val index: Int
    ) : AlbumViewerEvents
}

@Composable
fun ExtractorAlbumViewerScreen(
    modifier: Modifier = Modifier,
    state: ExtractorAlbumViewerState,
    dialogSelectionState: ExtractorAlbumDialogSelection,
    imageGridState: ExtractorGridState<Int>,
    snackbarHostState: SnackbarHostState,
    onDeleteDialogAction: (ConfirmationDialogActions) -> Unit,
    onShareDialogAction: (ConfirmationDialogActions) -> Unit,
    onMultiselectAction: (MultiselectAction) -> Unit,
) {
    val imageSize = 96
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // floating dialogs
        when (dialogSelectionState) {
            ExtractorAlbumDialogSelection.ConfirmDelete -> {
                ConfirmationDialog(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
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
                            state.entries.size
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            ExtractorAlbumDialogSelection.None -> Unit
        }

        // screen content
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Adaptive(imageSize.dp),
            state = imageGridState.lazyGridState,
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            // top bar
            maxLineSpanItem {
                ExtractorTopBar(
                    modifier = Modifier,
                    leadingSlot = {
                        BackIconButton(onBack = { state.eventSink(AlbumViewerEvents.GoBack) })
                    },
                    trailingSlot = {},
                    centerSlot = {},
                )
            }

            maxLineSpanItem {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // hero
            maxLineSpanItem(key = "hero_section") {
                AlbumHeroSection(
                    model = state.hero,
                    onShare = { state.eventSink(AlbumViewerEvents.Share) },
                    onDelete = { state.eventSink(AlbumViewerEvents.Delete) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            maxLineSpanItem {
                Column {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))
                }
            }

            itemsIndexed(
                state.entries,
                key = { _, it -> it.id.id }
            ) { index, entry ->
                ExtractorImageItem(
                    modifier = Modifier,
                    imageUri = entry.uri.toUri(),
                    size = imageSize,
                    onClick = {
                        val handled = imageGridState.onItemClick(index)
                        if (!handled) state.eventSink(AlbumViewerEvents.GoToImageViewer(index))
                    },
                    checkedState = imageGridState[index],
                    onLongClick = { imageGridState.onItemLongClick(index) }
                )
            }

            maxLineSpanItem {
                Spacer(Modifier.height(48.dp))
            }
        }

        AnimatedVisibility(
            visible = state.shouldShowSelectBar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorMultiselectActionBar(
                onAction = onMultiselectAction,
                items = albumGridActions
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            snackbar = { ExtractorSnackbar(snackbarData = it) }
        )
    }
}

@Stable
data class AlbumHeroUiModel(
    val name: String,
    val description: String,
    val heroImage: MediaImageUri
) {
    val displayName = "# $name"
}

@Composable
private fun AlbumHeroSection(
    modifier: Modifier = Modifier,
    model: AlbumHeroUiModel,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val fabElevation = 0.dp
    val surfaceSize = 96.dp
    val shape = RoundedCornerShape(28.dp)

    val imageUri = remember(model) {
        model.heroImage.toUri()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // title
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = model.displayName,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = model.description,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        // image/button section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .weight(0.67f)
                    .height(296.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp)),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Loaded image",
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shadowElevation = fabElevation,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .size(surfaceSize)
                        .alpha(0f),
                    shape = shape
                ) {
                }
                Surface(
                    onClick = onDelete,
                    shadowElevation = fabElevation,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(surfaceSize),
                    shape = shape
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Album"
                        )
                        Text(
                            stringResource(R.string.action_delete),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Surface(
                    onClick = onShare,
                    shadowElevation = fabElevation,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(surfaceSize),
                    shape = shape
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share Album"
                        )
                        Text(
                            stringResource(R.string.action_share),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
