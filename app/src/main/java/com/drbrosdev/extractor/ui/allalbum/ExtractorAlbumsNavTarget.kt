package com.drbrosdev.extractor.ui.allalbum

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.album.ExtractorAlbumNavTarget
import com.drbrosdev.extractor.ui.components.shared.ConfirmationDialogActions
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data object ExtractorAlbumsNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorAlbumsViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val navController = LocalNavController.current

        ExtractorAlbumsScreen(
            onBack = { navController.pop() },
            onAlbumClick = {
                navController.navigate(ExtractorAlbumNavTarget(it))
            },
            onDeleteClick = {
                viewModel.onDeleteAction(it)
            },
            onDeleteDialogAction = {
                when (it) {
                    ConfirmationDialogActions.Confirm -> viewModel.onDeleteAlbum()
                    else -> viewModel.onDismissDialog()
                }
            },
            state = state
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorAlbumsScreen(
                onBack = {},
                onAlbumClick = {},
                onDeleteClick = {},
                onDeleteDialogAction = {},
                state = ExtractorAlbumsScreenState.Loading
            )
        }
    }
}