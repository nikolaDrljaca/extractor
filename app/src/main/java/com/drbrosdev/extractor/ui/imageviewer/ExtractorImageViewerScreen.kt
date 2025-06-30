package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import com.drbrosdev.extractor.framework.rememberSystemBarsController
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorImageBottomBar
import com.drbrosdev.extractor.ui.components.shared.AppTooltip
import com.drbrosdev.extractor.util.asImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExtractorImageViewerScreen(
    onBottomBarClick: (ExtractorBottomBarItem) -> Unit,
    pagerState: PagerState,
    images: List<Uri>, // used as a lookup - only one images is displayed
    description: String?
) {
    var showUi by rememberSaveable {
        mutableStateOf(true)
    }
    val systemBarsController = rememberSystemBarsController()

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

            LaunchedEffect(showUi) {
                if (showUi) {
                    systemBarsController.show()
                } else {
                    systemBarsController.hide()
                }
            }

            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
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
            modifier = Modifier.layoutId(ViewIds.DESC_TOOLTIP),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (description != null) {
                AppTooltip(
                    text = description,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
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
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp)
            )
        }
    }
}

private fun imageDetailScreenConstraintSet() = ConstraintSet {
    val bottomBar = createRefFor(ViewIds.BOTTOM_BAR)
    val descriptionTooltip = createRefFor(ViewIds.DESC_TOOLTIP)
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

    constrain(descriptionTooltip) {
        bottom.linkTo(bottomBar.top, margin = 12.dp)
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
    }
}

private object ViewIds {
    const val BOTTOM_BAR = "bottomBar"
    const val PAGER = "pager"
    const val DESC_TOOLTIP = "descriptionTooltip"
}
