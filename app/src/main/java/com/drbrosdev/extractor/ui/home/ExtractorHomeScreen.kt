package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorAlbumOverviewContent
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorAlbumsUiModel
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorEmptyAlbumOverview
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageThumbnail

@Composable
fun ExtractorHomeScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onEmptyUserAlbums: () -> Unit,
    onCollageClicked: () -> Unit,
    collageThumbnail: ExtractorUserCollageThumbnailUiState,
    userAlbums: ExtractorAlbumsUiModel,
) {
    val lazyGridState = rememberLazyListState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (lazyGridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    Box(modifier = Modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = lazyGridState
        ) {
            item { Spacer(Modifier.height(64.dp)) }

            when (collageThumbnail) {
                ExtractorUserCollageThumbnailUiState.Empty -> Unit
                is ExtractorUserCollageThumbnailUiState.Content -> item {
                    ExtractorUserCollageThumbnail(
                        modifier = Modifier,
                        onClick = onCollageClicked,
                        imageUri = collageThumbnail.mediaImageUri,
                        keywords = collageThumbnail.keywords
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                Text(
                    text = stringResource(R.string.my_albums),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item { Spacer(Modifier.height(10.dp)) }

            when (userAlbums) {
                ExtractorAlbumsUiModel.Empty -> item {
                    ExtractorEmptyAlbumOverview(
                        onInitClick = onEmptyUserAlbums
                    )
                }

                is ExtractorAlbumsUiModel.Content -> items(
                    items = userAlbums.albums,
                    key = { it.albumId }
                ) {
                    ExtractorAlbumOverviewContent(
                        modifier = Modifier,
                        model = it,
                        onClick = {
                            userAlbums.onAlbumClick(it.albumId)
                        }
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }

        }

        ExtractorTopBar(
            modifier = Modifier,
            leadingSlot = {
                BackIconButton(onBack = onBack)
            },
            trailingSlot = {
                IconButton(onClick = onSettingsClick) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings")
                }
            },
            centerSlot = {},
            state = extractorTopBarState.value
        )
    }
}