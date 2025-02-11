package com.drbrosdev.extractor.ui.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorHomeScreenSettings
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerNavTarget
import com.drbrosdev.extractor.ui.allalbum.ExtractorAlbumsNavTarget
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.settings.ExtractorSettingsNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.ui.usercollage.ExtractorUserCollageNavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorHomeNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        // Bind the viewModel to the ActivityScope so it does not load data every time
        // Flows are hot anyways
        val viewModel: ExtractorHomeViewModel = koinViewModel(
            viewModelStoreOwner = LocalActivity.current as ComponentActivity
        )

        val navController = navigators.navController
        val dialogNavController = navigators.dialogNavController

        val visualAlbums by viewModel.visualAlbums.collectAsStateWithLifecycle()
        val userAlbums by viewModel.userAlbums.collectAsStateWithLifecycle()
        val textAlbums by viewModel.textAlbums.collectAsStateWithLifecycle()
        val settings by viewModel.settings.collectAsStateWithLifecycle()
        val collage by viewModel.collage.collectAsStateWithLifecycle()

        ExtractorHomeScreen(
            onSyncClick = { dialogNavController.navigate(ExtractorStatusDialogNavTarget) },
            onBack = { navController.pop() },
            visualAlbums = visualAlbums,
            userAlbums = userAlbums,
            textAlbums = textAlbums,
            settings = settings,
            collageThumbnail = collage,
            onInitTextPreview = viewModel::compileTextAlbums,
            onInitUserPreviews = { navController.pop() },
            onInitVisualPreview = viewModel::compileVisualAlbums,
            onAlbumPreviewClick = {
                navController.navigate(ExtractorAlbumViewerNavTarget(it))
            },
            onSettingsClick = {
                navController.navigate(ExtractorSettingsNavTarget)
            },
            onViewAllUserAlbums = {
                navController.navigate(ExtractorAlbumsNavTarget)
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
                onSyncClick = {},
                onBack = {},
                visualAlbums = ExtractorCategoryViewState.Initial(),
                userAlbums = ExtractorCategoryViewState.Initial(),
                textAlbums = ExtractorCategoryViewState.Initial(),
                collageThumbnail = ExtractorUserCollageThumbnailUiState.Empty,
                settings = ExtractorHomeScreenSettings(),
                onInitTextPreview = {},
                onInitUserPreviews = {},
                onInitVisualPreview = {},
                onAlbumPreviewClick = {},
                onSettingsClick = {},
                onViewAllUserAlbums = {},
                onCollageClicked = {}
            )
        }
    }
}
