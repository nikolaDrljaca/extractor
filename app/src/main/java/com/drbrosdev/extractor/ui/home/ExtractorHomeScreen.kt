package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorHomeScreenSettings
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorAlbumsViewDefaults
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryView
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorUserCategoryView
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageThumbnail
import com.drbrosdev.extractor.util.maxLineSpanItem

@Composable
fun ExtractorHomeScreen(
    onSyncClick: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onInitUserPreviews: () -> Unit,
    onInitVisualPreview: () -> Unit,
    onInitTextPreview: () -> Unit,
    onAlbumPreviewClick: (albumId: Long) -> Unit,
    onCollageClicked: () -> Unit,
    collageThumbnail: ExtractorUserCollageThumbnailUiState,
    visualAlbums: ExtractorCategoryViewState,
    userAlbums: ExtractorCategoryViewState,
    textAlbums: ExtractorCategoryViewState,
    settings: ExtractorHomeScreenSettings
) {
    val lazyGridState = rememberLazyGridState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (lazyGridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    Box(modifier = Modifier) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            columns = GridCells.Fixed(2),
            state = lazyGridState
        ) {
            maxLineSpanItem { Spacer(Modifier.height(64.dp)) }
            maxLineSpanItem {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedExtractorActionButton(
                        onClick = onSyncClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_sync_24),
                            contentDescription = "Sync"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.sync_status))
                    }

                    OutlinedExtractorActionButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.settings))
                    }
                }
            }

            when (collageThumbnail) {
                ExtractorUserCollageThumbnailUiState.Empty -> Unit
                is ExtractorUserCollageThumbnailUiState.Content -> maxLineSpanItem {
                    ExtractorUserCollageThumbnail(
                        modifier = Modifier,
                        onClick = onCollageClicked,
                        imageUri = collageThumbnail.mediaImageUri,
                        keywords = collageThumbnail.keywords
                    )
                }
            }

            ExtractorUserCategoryView(
                onAlbumPreviewClick = onAlbumPreviewClick,
                onInitClick = onInitUserPreviews,
                albumType = ExtractorAlbumsViewDefaults.AlbumType.USER,
                state = userAlbums,
                modifier = Modifier
            )

            if (settings.shouldShowVisualAlbums) maxLineSpanItem {
                ExtractorCategoryView(
                    onAlbumPreviewClick = onAlbumPreviewClick,
                    onInitClick = onInitVisualPreview,
                    albumType = ExtractorAlbumsViewDefaults.AlbumType.VISUAL,
                    state = visualAlbums,
                    modifier = Modifier
                )
            }

            if (settings.shouldShowTextAlbums) maxLineSpanItem {
                ExtractorCategoryView(
                    onAlbumPreviewClick = onAlbumPreviewClick,
                    onInitClick = onInitTextPreview,
                    albumType = ExtractorAlbumsViewDefaults.AlbumType.TEXT,
                    state = textAlbums,
                    modifier = Modifier
                )
            }
        }

        ExtractorTopBar(
            modifier = Modifier,
            leadingSlot = {
                BackIconButton(onBack = onBack)
                ExtractorHeader(headerText = stringResource(id = R.string.extractor_home))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            },
            centerSlot = {},
            state = extractorTopBarState.value
        )
    }
}