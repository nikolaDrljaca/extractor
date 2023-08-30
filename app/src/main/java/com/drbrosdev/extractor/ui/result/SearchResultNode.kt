package com.drbrosdev.extractor.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import org.koin.androidx.compose.koinViewModel

class SearchResultNode(
    buildContext: BuildContext,
    private val query: String
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: SearchResultViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(key1 = Unit) {
            viewModel.performSearch(query)
        }

        SearchResultScreen(
            state = state
        )
    }
}
