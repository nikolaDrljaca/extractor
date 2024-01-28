package com.drbrosdev.extractor.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGridState
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialog
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.components.shared.ExtractorAlbumDropdownMenu
import com.drbrosdev.extractor.ui.components.shared.ExtractorDropdownAction
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview

@Composable
fun ExtractorAlbumScreen(
    modifier: Modifier = Modifier,
    onImageClick: (index: Int) -> Unit,
    onDeleteDialogAction: (ConfirmationDialogActions) -> Unit,
    onShareDialogAction: (ConfirmationDialogActions) -> Unit,
    onDropdownAction: (ExtractorDropdownAction) -> Unit,
    onBack: () -> Unit,
    state: ExtractorAlbumScreenState,
    imageGridState: ExtractorImageGridState
) {
    val extractorTopBarState = remember {
        derivedStateOf {
            if (imageGridState.lazyGridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    when (state) {
        ExtractorAlbumScreenState.Loading -> ExtractorAlbumScreenLoading()
        is ExtractorAlbumScreenState.Content -> {
            if (state.isConfirmDeleteShown) {
                ConfirmationDialog(
                    icon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = "") },
                    onAction = onDeleteDialogAction
                ) {
                    Text(text = stringResource(R.string.album_delete_perm))
                }
            }

            if (state.isConfirmShareShown) {
                ConfirmationDialog(
                    icon = { Icon(imageVector = Icons.Rounded.Info, contentDescription = null) },
                    onAction = onShareDialogAction
                ) {
                    Text(
                        text = stringResource(R.string.too_many_images, state.album.entries.size),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
                            onDropdownAction = onDropdownAction,
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
                    ExtractorMultiselectActionBar(onAction = {})
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
    onDropdownAction: (ExtractorDropdownAction) -> Unit,
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

        ExtractorAlbumDropdownMenu(onAction = onDropdownAction)
    }
}


@Composable
@ScreenPreview
private fun CurrentPreview() {
    val data = ExtractorAlbumScreenState.Content(
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
        )
    )
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorAlbumScreen(
                state = data,
                imageGridState = ExtractorImageGridState(),
                onImageClick = {},
                onDropdownAction = {},
                onBack = {},
                onDeleteDialogAction = {},
                onShareDialogAction = {}
            )
        }
    }
}