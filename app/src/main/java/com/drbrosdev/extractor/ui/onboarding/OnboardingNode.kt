package com.drbrosdev.extractor.ui.onboarding

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.drbrosdev.extractor.R
import kotlinx.parcelize.Parcelize

sealed interface OnboardingRoutes : Parcelable {
    @Parcelize
    data object WelcomeScreen : OnboardingRoutes

    @Parcelize
    data object PrivacyNoteScreen : OnboardingRoutes

    @Parcelize
    data object PermissionScreen : OnboardingRoutes

    @Parcelize
    data object StartWorkerScreen : OnboardingRoutes
}

private val onBoardingStack = listOf(
    OnboardingRoutes.StartWorkerScreen,
    OnboardingRoutes.PermissionScreen,
    OnboardingRoutes.PrivacyNoteScreen,
    OnboardingRoutes.WelcomeScreen
)

class OnboardingNode(
    buildContext: BuildContext,
    private val backstack: BackStack<OnboardingRoutes> = BackStack(
        model = BackStackModel(
            initialTargets = onBoardingStack,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStack3D(it) },
        backPressStrategy = DontHandleBackPress()
    ),
) : ParentNode<OnboardingRoutes>(
    buildContext = buildContext,
    appyxComponent = backstack
) {
    @Composable
    override fun View(modifier: Modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
        ) {
            val topGuideline = createGuidelineFromTop(0.15f)
            val (header, cards) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(
                        ref = header,
                        constrainBlock = {
                            top.linkTo(topGuideline)
                        }
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            AppyxComponent(
                appyxComponent = backstack,
                modifier = modifier.constrainAs(cards) {
                    top.linkTo(header.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }

    override fun onChildFinished(child: Node) {
        backstack.pop()
    }

    override fun resolve(interactionTarget: OnboardingRoutes, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            OnboardingRoutes.WelcomeScreen -> WelcomeNode(buildContext)
            OnboardingRoutes.PrivacyNoteScreen -> PrivacyNoteNode(buildContext)
            OnboardingRoutes.PermissionScreen -> PermissionNode(buildContext)
            OnboardingRoutes.StartWorkerScreen -> StartWorkerNode(buildContext)
        }
    }
}