package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import coil3.compose.AsyncImage
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorImageBottomBar
import com.drbrosdev.extractor.util.asImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExtractorImageViewerScreen(
    onBottomBarClick: (ExtractorBottomBarItem) -> Unit,
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
                model = images[it].asImageRequest(LocalContext.current),
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    zoomState.setContentSize(state.painter.intrinsicSize)
                }
            )
        }

        AnimatedVisibility(
            visible = showUi,
            modifier = Modifier.layoutId(ViewIds.BOTTOM_BAR),
            enter = slideInVertically(
                animationSpec = MotionScheme.expressive().slowEffectsSpec(),
                initialOffsetY = { it }
            ),
            exit = slideOutVertically(
                animationSpec = MotionScheme.expressive().slowEffectsSpec(),
                targetOffsetY = { it }
            )
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
    }
}

private object ViewIds {
    const val BOTTOM_BAR = "bottomBar"
    const val PAGER = "pager"
}
