package com.drbrosdev.extractor.ui.home

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import org.koin.androidx.compose.koinViewModel

class HomeNode(
    buildContext: BuildContext,
    private val onImageClick: (NavToImageNodePayload) -> Unit
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        HomeScreen(
            state = state,
            onEvent = viewModel::consumeEvent,
            onNavToImageNode = { images, index ->
                val payload = NavToImageNodePayload(
                    images = images.map { it.uri },
                    initialIndex = index
                )
                onImageClick(payload)
            }
        )
    }
}


data class NavToImageNodePayload(
    val images: List<Uri>,
    val initialIndex: Int
)
