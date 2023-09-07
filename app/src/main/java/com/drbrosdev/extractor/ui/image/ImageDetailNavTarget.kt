package com.drbrosdev.extractor.ui.image

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.util.NavTarget
import kotlinx.parcelize.Parcelize
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class ImageDetailNavTarget(
    private val images: List<Uri>,
    private val initialIndex: Int
) : NavTarget {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }
        val viewModel: ImageDetailViewModel = koinViewModel()
        val currentImageInfo by viewModel.state.collectAsState()

        LaunchedEffect(key1 = Unit) {
            snapshotFlow { pagerState.currentPage }
                .collect {
                    viewModel.loadImageDetails(images[it])
                }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .systemBarsPadding()
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                verticalAlignment = Alignment.CenterVertically,
                pageSize = PageSize.Fill
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
                        .zoomable(zoomState),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[it])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.baseline_image_24),
                    onSuccess = { state ->
                        zoomState.setContentSize(state.painter.intrinsicSize)
                    }
                )
            }
        }
    }
}