package com.drbrosdev.extractor.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.drbrosdev.extractor.ui.home.HomeNode
import com.drbrosdev.extractor.ui.result.SearchResultNode


class MainNode(
    buildContext: BuildContext,
    private val backstack: BackStack<MainRoutes> = BackStack(
        model = BackStackModel(
            initialTarget = MainRoutes.HomeRoute,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackSlider(it) },
    ),
) : ParentNode<MainRoutes>(
    buildContext = buildContext,
    appyxComponent = backstack
), MainNavigator {

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(backstack)
    }

    override fun resolve(interactionTarget: MainRoutes, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            MainRoutes.AboutRoute -> node(buildContext) { }
            MainRoutes.HomeRoute -> HomeNode(buildContext, this)
            is MainRoutes.SearchResultRoute -> SearchResultNode(buildContext, interactionTarget.query)
            MainRoutes.SyncStatusRoute -> node(buildContext) { /*TODO*/ }
        }
    }

    override fun toSearchResultRoute(args: SearchResultRouteArgs) {
        backstack.push(
            MainRoutes.SearchResultRoute(args.query)
        )
    }
}