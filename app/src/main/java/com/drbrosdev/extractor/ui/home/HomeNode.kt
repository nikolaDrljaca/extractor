package com.drbrosdev.extractor.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.ui.main.MainNavigator
import com.drbrosdev.extractor.ui.main.NavToImageNodeArgs
import org.koin.androidx.compose.koinViewModel

class HomeNode(
    buildContext: BuildContext,
    private val navigator: MainNavigator
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: HomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        HomeScreen(
            state = state,
            onEvent = viewModel::consumeEvent,
            onNavToImageNode = { images, index ->
                val args = NavToImageNodeArgs(
                    images = images.map { it.uri },
                    initialIndex = index
                )
                navigator.toImageDetailRoute(args)
            }
        )
    }
}


