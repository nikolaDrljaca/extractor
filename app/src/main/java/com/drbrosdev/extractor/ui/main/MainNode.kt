package com.drbrosdev.extractor.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.drbrosdev.extractor.SearchScreen

class MainNode(
    buildContext: BuildContext
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        SearchScreen(
            onOpenAppSettings = {},
            onNavigateToPager = {}
        )
    }
}