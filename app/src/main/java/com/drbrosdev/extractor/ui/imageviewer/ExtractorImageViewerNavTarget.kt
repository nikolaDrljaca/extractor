package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.LocalBottomSheetNavController
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchEditIntent
import com.drbrosdev.extractor.util.launchShareIntent
import com.drbrosdev.extractor.util.launchUseAsIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class ExtractorImageViewerNavTarget(
    private val images: List<Uri>,
    private val initialIndex: Int
) : NavTarget {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: ExtractorImageViewerModel = koinViewModel()

        val currentImageInfo by viewModel.currentMediaImageInfo.collectAsStateWithLifecycle()
        val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }

        val context = LocalContext.current
        val navController = LocalNavController.current
        val bottomSheetNavigator = LocalBottomSheetNavController.current

        LaunchedEffect(key1 = Unit) {
            snapshotFlow { pagerState.currentPage }
                .collect {
                    viewModel.loadImageDetails(images[it])
                }
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.events.collect { event ->
                currentImageInfo?.let { imageInfo ->
                    when (event) {
                        ExtractorImageViewerEvents.OnEdit -> context.launchEditIntent(imageInfo)
                        ExtractorImageViewerEvents.OnExtractorInfo ->
                            bottomSheetNavigator.navigate(ExtractorImageInfoNavTarget(imageInfo.mediaImageId))
                        ExtractorImageViewerEvents.OnShare -> context.launchShareIntent(imageInfo)
                        ExtractorImageViewerEvents.OnUseAs -> context.launchUseAsIntent(imageInfo)
                    }
                }
            }
        }

        ExtractorImageViewerScreen(
            pagerState = pagerState,
            images = images,
            onBack = { navController.pop() },
            onBottomBarClick = { viewModel.processEvent(it) }
        )
    }
}

@ScreenPreview
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorImageViewerScreen(
            onBottomBarClick = {},
            onBack = { /*TODO*/ },
            pagerState = rememberPagerState { 0 },
            images = emptyList()
        )
    }
}