package com.drbrosdev.extractor.ui.allalbum

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.album.ExtractorAlbumNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchShareIntent
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
        val context = LocalContext.current

        ExtractorAlbumsScreen(
            onBack = { navController.pop() },
            onAlbumClick = {
                navController.navigate(ExtractorAlbumNavTarget(it))
            },
            onAction = {
                when (it) {
                    is ExtractorSwipeAction.Delete -> viewModel.onDeleteAlbum(it.value)
                    is ExtractorSwipeAction.Share -> {
                        viewModel.onShareAlbum(it.value)
                        context.launchShareIntent(it.value.thumbnails)
                    }
                }
            },
            state = state
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    val state = ExtractorAlbumsScreenState.Content(
        albums = buildList {
            repeat(4) {
                add(AlbumItemUiModel(
                    keyword = "keyword",
                    metadata = "metadata",
                    id = it.toLong(),
                    thumbnails = buildList {
                        repeat(4) { inner ->
                            add("$inner xx".toUri())
                        }
                    }
                ))
            }
        },
        snackBarHostState = SnackbarHostState()
    )

    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorAlbumsScreen(
                onBack = {},
                onAlbumClick = {},
                onAction = {},
                state = state
            )
        }
    }
}