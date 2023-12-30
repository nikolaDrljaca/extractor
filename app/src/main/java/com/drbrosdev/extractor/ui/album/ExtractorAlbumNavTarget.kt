package com.drbrosdev.extractor.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
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

        ExtractorAlbumScreen(
            onImageClick = { index ->
                val destination = ExtractorImageNavTarget(
                    images = viewModel.imageUris.value,
                    initialIndex = index
                )
                navController.navigate(destination)
            },
            state = state,
            onDropdownAction = {},
            onBack = { navController.pop() }
        )
    }
}