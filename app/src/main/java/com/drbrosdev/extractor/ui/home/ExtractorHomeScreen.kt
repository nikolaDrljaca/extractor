package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorHomeScreenSettings
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorAlbumsViewDefaults
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryView
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageThumbnail

@Composable
fun ExtractorHomeScreen(
    onSyncClick: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onInitUserPreviews: () -> Unit,
    onInitVisualPreview: () -> Unit,
    onInitTextPreview: () -> Unit,
    onAlbumPreviewClick: (albumId: Long) -> Unit,
    onViewAllUserAlbums: () -> Unit,
    onCollageClicked: () -> Unit,
    collageThumbnail: ExtractorUserCollageThumbnailUiState,
    visualAlbums: ExtractorCategoryViewState,
    userAlbums: ExtractorCategoryViewState,
    textAlbums: ExtractorCategoryViewState,
    settings: ExtractorHomeScreenSettings
) {
    val scrollState = rememberScrollState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (scrollState.value > 100) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    Box(modifier = Modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(scrollState),
            constraintSet = homeScreenConstraintSet()
        ) {
            when (collageThumbnail) {
                ExtractorUserCollageThumbnailUiState.Empty -> Unit
                is ExtractorUserCollageThumbnailUiState.Content -> {
                    ExtractorUserCollageThumbnail(
                        modifier = Modifier.layoutId(ViewIds.COLLAGE),
                        onClick = onCollageClicked,
                        imageUri = collageThumbnail.mediaImageUri,
                        keywords = collageThumbnail.keywords
                    )
                }
            }

            ExtractorCategoryView(
                onViewAllClicked = onViewAllUserAlbums,
                onAlbumPreviewClick = onAlbumPreviewClick,
                onInitClick = onInitUserPreviews,
                contentPadding = PaddingValues(horizontal = 12.dp),
                category = ExtractorAlbumsViewDefaults.Category.USER,
                state = userAlbums,
                modifier = Modifier.layoutId(ViewIds.USER_ALBUM)
            )

            if (settings.shouldShowVisualAlbums) {
                ExtractorCategoryView(
                    onViewAllClicked = {},
                    onAlbumPreviewClick = onAlbumPreviewClick,
                    onInitClick = onInitVisualPreview,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    category = ExtractorAlbumsViewDefaults.Category.VISUAL,
                    state = visualAlbums,
                    modifier = Modifier.layoutId(ViewIds.VISUAL_ALBUM)
                )
            }

            if (settings.shouldShowTextAlbums) {
                ExtractorCategoryView(
                    onViewAllClicked = { },
                    onAlbumPreviewClick = onAlbumPreviewClick,
                    onInitClick = onInitTextPreview,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    category = ExtractorAlbumsViewDefaults.Category.TEXT,
                    state = textAlbums,
                    modifier = Modifier.layoutId(ViewIds.TEXT_ALBUM)
                )
            }

            OutlinedExtractorActionButton(
                onClick = onSyncClick,
                modifier = Modifier.layoutId(ViewIds.SYNC_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_sync_24),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.sync_status))
            }

            OutlinedExtractorActionButton(
                onClick = onSettingsClick,
                modifier = Modifier.layoutId(ViewIds.SETTINGS_BUTTON)
            ) {
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.settings))
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

private fun homeScreenConstraintSet() = ConstraintSet {
    val settingsButton = createRefFor(ViewIds.SETTINGS_BUTTON)
    val syncButton = createRefFor(ViewIds.SYNC_BUTTON)

    val collage = createRefFor(ViewIds.COLLAGE)

    val userAlbum = createRefFor(ViewIds.USER_ALBUM)
    val textAlbum = createRefFor(ViewIds.TEXT_ALBUM)
    val visualAlbum = createRefFor(ViewIds.VISUAL_ALBUM)

    val buttonGuideline = createGuidelineFromStart(0.5f)
    val topGuideline = createGuidelineFromTop(0.1f)

    constrain(settingsButton) {
        start.linkTo(buttonGuideline, margin = 4.dp)
        top.linkTo(topGuideline, margin = 8.dp)
        end.linkTo(parent.end, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(collage) {
        start.linkTo(parent.start, margin = 12.dp)
        end.linkTo(parent.end, margin = 12.dp)
        top.linkTo(syncButton.bottom, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(syncButton) {
        top.linkTo(topGuideline, margin = 8.dp)
        start.linkTo(parent.start, margin = 12.dp)
        end.linkTo(buttonGuideline, margin = 4.dp)
        width = Dimension.fillToConstraints
    }

    constrain(userAlbum) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(collage.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(visualAlbum) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(userAlbum.bottom)
        width = Dimension.fillToConstraints
    }

    constrain(textAlbum) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(visualAlbum.bottom)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val SYNC_BUTTON = "syncButton"
    const val SETTINGS_BUTTON = "settingsButton"
    const val USER_ALBUM = "userAlbum"
    const val TEXT_ALBUM = "textAlbum"
    const val VISUAL_ALBUM = "visualAlbum"
    const val COLLAGE = "collage"
}

