package com.drbrosdev.extractor.ui.root

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.drbrosdev.extractor.ui.main.MainNode
import com.drbrosdev.extractor.ui.onboarding.OnboardingNode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


sealed interface RootRoutes : Parcelable {

    @Parcelize
    data object Main : RootRoutes

    @Parcelize
    data object Onboarding : RootRoutes
}

private val initialStack = listOf(
    RootRoutes.Main,
    RootRoutes.Onboarding
)

class RootNode(
    buildContext: BuildContext,
    private val backstack: BackStack<RootRoutes> = BackStack(
        model = BackStackModel(
            initialTarget = RootRoutes.Main,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackFader(it) }
    )
) : ParentNode<RootRoutes>(
    buildContext = buildContext,
    appyxComponent = backstack
) {

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: RootViewModel = koinViewModel()
        LaunchedEffect(key1 = Unit) {
            viewModel.isOnboardingFinished.collect {
                if (!it) backstack.push(RootRoutes.Onboarding)
            }
        }

        AppyxComponent(appyxComponent = backstack)
    }

    override fun onChildFinished(child: Node) {
        backstack.pop()
    }

    override fun resolve(interactionTarget: RootRoutes, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            RootRoutes.Main -> MainNode(buildContext)
            RootRoutes.Onboarding -> OnboardingNode(buildContext)
        }
    }
}
