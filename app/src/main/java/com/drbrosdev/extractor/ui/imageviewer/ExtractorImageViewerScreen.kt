package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorImageBottomBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageTopBar
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorImageViewerScreen(
    onBottomBarClick: (ExtractorBottomBarItem) -> Unit,
    onBack: () -> Unit,
    pagerState: PagerState,
    images: List<Uri>,
) {
    var showUi by rememberSaveable {
        mutableStateOf(true)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        constraintSet = imageDetailScreenConstraintSet()
    ) {
        HorizontalPager(
            modifier = Modifier.layoutId(ViewIds.PAGER),
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically,
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 1
        ) {
            val zoomState = rememberZoomState()

            //reset zoom state when swiped off the screen
            val isVisible = it == pagerState.settledPage
            LaunchedEffect(isVisible) {
                zoomState.reset()
            }

            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .zoomable(
                        zoomState = zoomState,
                        onTap = { showUi = !showUi }
                    ),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[it])
                    .crossfade(true)
                    .build(),
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                onSuccess = { state ->
                    zoomState.setContentSize(state.painter.intrinsicSize)
                }
            )
        }

        AnimatedVisibility(
            visible = showUi,
            modifier = Modifier
                .layoutId(ViewIds.TOP_BAR),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorImageTopBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(12.dp),
                onBackClick = onBack,
            )
        }

        AnimatedVisibility(
            visible = showUi,
            modifier = Modifier.layoutId(ViewIds.BOTTOM_BAR),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorImageBottomBar(
                onClick = onBottomBarClick,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

private fun imageDetailScreenConstraintSet() = ConstraintSet {
    val bottomBar = createRefFor(ViewIds.BOTTOM_BAR)
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val pager = createRefFor(ViewIds.PAGER)

    constrain(pager) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(bottomBar) {
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(topBar) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val TOP_BAR = "topBar"
    const val BOTTOM_BAR = "bottomBar"
    const val PAGER = "pager"
}
