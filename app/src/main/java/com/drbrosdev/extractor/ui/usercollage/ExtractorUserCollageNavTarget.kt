package com.drbrosdev.extractor.ui.usercollage

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
object ExtractorUserCollageNavTarget : NavTarget {

    @Composable
    override fun Content() {

        val viewModel: ExtractorUserCollageViewModel = koinViewModel()
        val navController = LocalNavController.current
        val context = LocalContext.current

        val state by viewModel.userCollageState.collectAsStateWithLifecycle()
        val showBanner by viewModel.showBanner.collectAsStateWithLifecycle()

        CollectFlow(flow = viewModel.events) {
            when (it) {
                is ExtractorUserCollageEvents.NavToImageViewer -> {
                    navController.navigate(
                        ExtractorImageViewerNavTarget(
                            images = it.images,
                            initialIndex = it.initialIndex
                        )
                    )
                }

                is ExtractorUserCollageEvents.ShareCollage -> {
                    context.launchShareIntent(it.images)
                }
            }
        }

        ExtractorUserCollageScreen(
            state = state,
            showBanner = showBanner,
            onBack = { navController.pop() },
            onHideBanner = { viewModel.hideYourKeywordsBannerBanner() }
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorUserCollageScreen(
                state = ExtractorUserCollageUiState.Loading,
                showBanner = false,
                onBack = {},
                onHideBanner = {}
            )
        }
    }
}