package com.drbrosdev.extractor.ui.imageviewer

import android.net.Uri
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchEditIntent
import com.drbrosdev.extractor.util.launchShareIntent
import com.drbrosdev.extractor.util.launchUseAsIntent
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorImageViewerNavTarget(
    private val images: List<Uri>,
    private val initialIndex: Int
) : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorImageViewerModel = koinViewModel {
            parametersOf(images, initialIndex)
        }

        val currentImageInfo by viewModel.mediaImageInfo.collectAsStateWithLifecycle()
        val annotations by viewModel.annotations.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val navController = navigators.navController

        CollectFlow(viewModel.events) {
            currentImageInfo?.let { imageInfo ->
                when (it) {
                    ExtractorImageViewerEvents.OnEdit -> context.launchEditIntent(imageInfo)
                    ExtractorImageViewerEvents.OnExtractorInfo ->
                        navController.navigate(ExtractorImageInfoNavTarget(imageInfo.mediaImageId))
                    ExtractorImageViewerEvents.OnShare -> context.launchShareIntent(imageInfo)
                    ExtractorImageViewerEvents.OnUseAs -> context.launchUseAsIntent(imageInfo)
                }
            }
        }

        ExtractorImageViewerScreen(
            pagerState = viewModel.pagerState,
            images = images,
            onBottomBarClick = { viewModel.processEvent(it) },
            description = annotations
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorImageViewerScreen(
            onBottomBarClick = {},
            pagerState = rememberPagerState { 0 },
            images = emptyList(),
            description = "some tooltip bababui"
        )
    }
}