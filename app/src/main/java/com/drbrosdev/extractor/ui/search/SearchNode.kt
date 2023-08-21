package com.drbrosdev.extractor.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import org.koin.androidx.compose.koinViewModel

class SearchNode(
    buildContext: BuildContext
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: SearchViewModel = koinViewModel()
        val syncStatus by viewModel.syncStatus.collectAsState()

        SearchScreen(syncStatus)
    }
}

