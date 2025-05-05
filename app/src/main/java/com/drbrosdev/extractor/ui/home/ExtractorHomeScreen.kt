package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorAlbumsViewDefaults
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorUserCategoryView
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageThumbnail
import com.drbrosdev.extractor.util.maxLineSpanItem

@Composable
fun ExtractorHomeScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onInitUserPreviews: () -> Unit,
    onAlbumPreviewClick: (albumId: Long) -> Unit,
    onCollageClicked: () -> Unit,
    collageThumbnail: ExtractorUserCollageThumbnailUiState,
    userAlbums: ExtractorCategoryViewState,
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

            maxLineSpanItem { Spacer(Modifier.height(16.dp)) }

            ExtractorUserCategoryView(
                onAlbumPreviewClick = onAlbumPreviewClick,
                onInitClick = onInitUserPreviews,
                albumType = ExtractorAlbumsViewDefaults.AlbumType.USER,
                state = userAlbums,
                modifier = Modifier,
                contentPadding = PaddingValues(top = 6.dp)
            )

            maxLineSpanItem {
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