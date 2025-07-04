package com.drbrosdev.extractor.ui.usercollage

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.model.LupaBundle
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Parcelize
object ExtractorUserCollageNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {

        val viewModel: ExtractorUserCollageViewModel = koinViewModel()
        val navController = navigators.navController
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
            onHideBanner = viewModel::hideYourKeywordsBanner
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        val lupaImageMetadata = listOf(
            LupaImageMetadata(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(1L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            LupaImageMetadata(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(2L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            LupaImageMetadata(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(3L),
                path = "",
                dateAdded = LocalDateTime.now()
            ),
            LupaImageMetadata(
                uri = MediaImageUri(""),
                mediaImageId = MediaImageId(4L),
                path = "",
                dateAdded = LocalDateTime.now()
            )
        )
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorUserCollageScreen(
                state = ExtractorUserCollageUiState.Content(
                    collages = listOf(LupaBundle("sample", lupaImageMetadata)),
                    onShare = {},
                    onItemClicked = { _, _ -> }
                ),
                showBanner = false,
                onBack = {},
                onHideBanner = {}
            )
        }
    }
}