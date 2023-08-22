package com.drbrosdev.extractor.ui.main

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.drbrosdev.extractor.ui.home.HomeNode
import com.drbrosdev.extractor.ui.image.ImageNode
import kotlinx.parcelize.Parcelize

sealed interface MainRoutes : Parcelable {

    @Parcelize
    data object SearchRoute : MainRoutes

    @Parcelize
    data class ImageDetailRoute(
        val images: List<Uri>,
        val initialIndex: Int
    ) : MainRoutes

    @Parcelize
    data object AboutRoute : MainRoutes
}

class MainNode(
    buildContext: BuildContext,
    private val backstack: BackStack<MainRoutes> = BackStack(
        model = BackStackModel(
            initialTarget = MainRoutes.SearchRoute,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackFader(it) },
//        backPressStrategy = DontHandleBackPress()
    ),
) : ParentNode<MainRoutes>(
    buildContext = buildContext,
    appyxComponent = backstack
) {

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(backstack)
    }

    override fun resolve(interactionTarget: MainRoutes, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            MainRoutes.AboutRoute -> node(buildContext) { }
            is MainRoutes.ImageDetailRoute -> ImageNode(
                images = interactionTarget.images,
                initialIndex = interactionTarget.initialIndex,
                buildContext = buildContext
            )

            MainRoutes.SearchRoute -> HomeNode(buildContext) {
                backstack.push(
                    MainRoutes.ImageDetailRoute(
                        images = it.images,
                        initialIndex = it.initialIndex
                    )
                )
            }
        }
    }
}