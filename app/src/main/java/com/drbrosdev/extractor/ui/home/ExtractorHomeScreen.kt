package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorAlbumsViewDefaults
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryView
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton

@Composable
fun ExtractorHomeScreen(
    onSyncClick: () -> Unit,
    onBack: () -> Unit,
    onInitUserPreviews: () -> Unit,
    onInitVisualPreview: () -> Unit,
    onInitTextPreview: () -> Unit,
    visualAlbums: ExtractorCategoryViewState,
    userAlbums: ExtractorCategoryViewState,
    textAlbums: ExtractorCategoryViewState,
) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(scrollState),
        constraintSet = homeScreenConstraintSet()
    ) {
        ExtractorCategoryView(
            onViewAllClicked = { /*TODO*/ },
            onAlbumPreviewClick = {},
            onInitClick = onInitUserPreviews,
            contentPadding = PaddingValues(horizontal = 12.dp),
            category = ExtractorAlbumsViewDefaults.Category.USER,
            state = userAlbums,
            modifier = Modifier.layoutId(ViewIds.USER_ALBUM)
        )

        ExtractorCategoryView(
            onViewAllClicked = {},
            onAlbumPreviewClick = {},
            onInitClick = onInitVisualPreview,
            contentPadding = PaddingValues(horizontal = 12.dp),
            category = ExtractorAlbumsViewDefaults.Category.VISUAL,
            state = visualAlbums,
            modifier = Modifier.layoutId(ViewIds.VISUAL_ALBUM)
        )

        ExtractorCategoryView(
            onViewAllClicked = { /*TODO*/ },
            onAlbumPreviewClick = {},
            onInitClick = onInitTextPreview,
            contentPadding = PaddingValues(horizontal = 12.dp),
            category = ExtractorAlbumsViewDefaults.Category.TEXT,
            state = textAlbums,
            modifier = Modifier.layoutId(ViewIds.TEXT_ALBUM)
        )

        ExtractorTopBar(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            leadingSlot = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = ""
                    )
                }
                ExtractorHeader(headerText = stringResource(id = R.string.extractor_home))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )

        OutlinedExtractorActionButton(
            onClick = onSyncClick,
            modifier = Modifier.layoutId(ViewIds.SYNC_BUTTON)
        ) {
            Icon(painter = painterResource(id = R.drawable.round_sync_24), contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.sync_status))
        }

        OutlinedExtractorActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.layoutId(ViewIds.SETTINGS_BUTTON)
        ) {
            Icon(imageVector = Icons.Rounded.Settings, contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.settings))
        }

    }
}

private fun homeScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val settingsButton = createRefFor(ViewIds.SETTINGS_BUTTON)
    val syncButton = createRefFor(ViewIds.SYNC_BUTTON)

    val userAlbum = createRefFor(ViewIds.USER_ALBUM)
    val textAlbum = createRefFor(ViewIds.TEXT_ALBUM)
    val visualAlbum = createRefFor(ViewIds.VISUAL_ALBUM)

    val buttonGuideline = createGuidelineFromStart(0.5f)

    constrain(settingsButton) {
        start.linkTo(buttonGuideline, margin = 4.dp)
        top.linkTo(topBar.bottom, margin = 8.dp)
        end.linkTo(parent.end, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(syncButton) {
        top.linkTo(topBar.bottom, margin = 8.dp)
        start.linkTo(parent.start, margin = 12.dp)
        end.linkTo(buttonGuideline, margin = 4.dp)
        width = Dimension.fillToConstraints
    }

    constrain(topBar) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(userAlbum) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(settingsButton.bottom, margin = 8.dp)
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
    const val TOP_BAR = "topBar"
    const val SYNC_BUTTON = "syncButton"
    const val SETTINGS_BUTTON = "settingsButton"
    const val USER_ALBUM = "userAlbum"
    const val TEXT_ALBUM = "textAlbum"
    const val VISUAL_ALBUM = "visualAlbum"
}

