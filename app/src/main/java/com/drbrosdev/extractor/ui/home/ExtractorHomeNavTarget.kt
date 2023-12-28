package com.drbrosdev.extractor.ui.home

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.album.ExtractorAlbumNavTarget
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalDialogNavController
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorHomeNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorHomeViewModel = koinViewModel()

        val navController = LocalNavController.current
        val dialogNavController = LocalDialogNavController.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val visualAlbums by viewModel.visualAlbums.collectAsStateWithLifecycle()
        val userAlbums by viewModel.userAlbums.collectAsStateWithLifecycle()
        val textAlbums by viewModel.textAlbums.collectAsStateWithLifecycle()


        ExtractorHomeScreen(
            onSyncClick = { dialogNavController.navigate(ExtractorStatusDialogNavTarget) },
            onBack = { navController.pop() },
            visualAlbums = visualAlbums,
            userAlbums = userAlbums,
            textAlbums = textAlbums,
            onInitTextPreview = {},
            onInitUserPreviews = { navController.pop() },
            onInitVisualPreview = viewModel::compileVisualAlbums,
            onAlbumPreviewClick = {
                navController.navigate(ExtractorAlbumNavTarget(it))
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
                visualAlbums = ExtractorCategoryViewState.Initial,
                userAlbums = ExtractorCategoryViewState.Initial,
                textAlbums = ExtractorCategoryViewState.Initial,
                onInitTextPreview = {},
                onInitUserPreviews = {},
                onInitVisualPreview = {},
                onAlbumPreviewClick = {}
            )
        }
    }
}
