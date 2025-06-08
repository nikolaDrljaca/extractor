package com.drbrosdev.extractor.ui.home

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorAlbumsUiModel
import com.drbrosdev.extractor.ui.settings.ExtractorSettingsNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.usercollage.ExtractorUserCollageNavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Parcelize
object ExtractorHomeNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorHomeViewModel = koinViewModel {
            parametersOf(navigators)
        }

        val navController = navigators.navController

        val userAlbums by viewModel.userAlbums.collectAsStateWithLifecycle()
        val collage by viewModel.collage.collectAsStateWithLifecycle()

        ExtractorHomeScreen(
            onBack = { navController.pop() },
            userAlbums = userAlbums,
            collageThumbnail = collage,
            onEmptyUserAlbums = { navController.pop() },
            onSettingsClick = {
                navController.navigate(ExtractorSettingsNavTarget)
            },
            onCollageClicked = {
                navController.navigate(ExtractorUserCollageNavTarget)
            }
        )
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorHomeScreen(
                onBack = {},
                userAlbums = ExtractorAlbumsUiModel.Empty,
                collageThumbnail = ExtractorUserCollageThumbnailUiState.Empty,
                onEmptyUserAlbums = {},
                onSettingsClick = {},
                onCollageClicked = {}
            )
        }
    }
}
