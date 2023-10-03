package com.drbrosdev.extractor.ui.image

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.launchEditIntent
import com.drbrosdev.extractor.util.launchShareIntent
import com.drbrosdev.extractor.util.launchUseAsIntent
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
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
        val context = LocalContext.current
        val navController = LocalNavController.current
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = Unit) {
            snapshotFlow { pagerState.currentPage }
                .collect {
                    viewModel.loadImageDetails(images[it])
                }
        }

        ImageDetailScreen(
            pagerState = pagerState,
            images = images,
            onBack = { navController.pop() },
            onImageInfo = { /*TODO*/ },
            onBottomBarClick = {
                when (it) {
                    ExtractorBottomBarItem.SHARE -> {
                        currentImageInfo?.let { imageInfo ->
                            scope.launch {
                                context.launchShareIntent(imageInfo)
                            }
                        }
                    }

                    ExtractorBottomBarItem.EDIT -> {
                        currentImageInfo?.let { imageInfo ->
                            scope.launch {
                                context.launchEditIntent(imageInfo)
                            }
                        }
                    }

                    ExtractorBottomBarItem.USE_AS -> {
                        currentImageInfo?.let { imageInfo ->
                            scope.launch {
                                context.launchUseAsIntent(imageInfo)
                            }
                        }
                    }

                    ExtractorBottomBarItem.EX_INFO -> {}
                }
            }
        )
    }
}