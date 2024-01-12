package com.drbrosdev.extractor.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.launchShareIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class ExtractorAlbumNavTarget(
    private val albumId: Long
) : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorAlbumViewModel = koinViewModel {
            parametersOf(albumId)
        }
        val state by viewModel.state.collectAsStateWithLifecycle()

        val navController = LocalNavController.current
        val context = LocalContext.current

        ExtractorAlbumScreen(
            onImageClick = { index ->
                val destination = ExtractorImageNavTarget(
                    images = viewModel.imageUris.value,
                    initialIndex = index
                )
                navController.navigate(destination)
            },
            state = state,
            onDropdownAction = { action ->
                when (action) {
                    AlbumHeaderDropdownAction.Delete -> {
                        viewModel.onDeleteAction()
                    }

                    AlbumHeaderDropdownAction.Share -> {
                        context.launchShareIntent(viewModel.imageUris.value)
                    }
                }
            },
            onBack = { navController.pop() },
            onDialogAction = {
                when (it) {
                    ConfirmationDialogActions.Confirm -> {
                        viewModel.onDeleteAlbum()
                        navController.pop()
                    }
                    ConfirmationDialogActions.Deny -> viewModel.onDismissDialog()
                    ConfirmationDialogActions.Dismiss -> viewModel.onDismissDialog()
                }
            }
        )
    }
}