package com.drbrosdev.extractor.ui.onboarding

import android.os.Parcelable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.interactions.core.model.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerNode
import com.drbrosdev.extractor.ui.theme.md_theme_light_secondary
import com.drbrosdev.extractor.ui.theme.md_theme_light_tertiary
import com.drbrosdev.extractor.util.adaptiveIconPainterResource
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
        val animation = rememberInfiniteTransition(label = "brush")
        val flat = with(LocalDensity.current) { 800.dp.toPx() }

        val offset by animation.animateFloat(
            initialValue = 0f,
            targetValue = flat,
            animationSpec = infiniteRepeatable(
                tween(
                    durationMillis = 4000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "brush"
        )

        val brush = Brush.linearGradient(
            listOf(
                md_theme_light_tertiary,
                md_theme_light_secondary,
            ),
            start = Offset(offset, offset),
            end = Offset(offset + flat, offset + flat),
            tileMode = TileMode.Mirror
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
        ) {
            val topGuideline = createGuidelineFromTop(0.05f)
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
            ) {
                Icon(
                    painter = adaptiveIconPainterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "App Icon"
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
            }

            Box(modifier = modifier.constrainAs(cards) {
                top.linkTo(header.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 48.dp)
                height = Dimension.fillToConstraints
            }) {
                AppyxComponent(
                    appyxComponent = backstack,
                )
            }
        }
    }

    override fun onChildFinished(child: Node) {
        if (backstack.elements.value.all.size == 1) {
            finish()
        }
        backstack.pop()
    }

    override fun resolve(interactionTarget: OnboardingRoutes, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            OnboardingRoutes.WelcomeScreen -> WelcomeNode(buildContext)
            OnboardingRoutes.PrivacyNoteScreen -> PrivacyNoteNode(
                buildContext = buildContext,
                onBackPressed = { backstack.push(OnboardingRoutes.WelcomeScreen) }
            )

            OnboardingRoutes.PermissionScreen -> PermissionNode(
                buildContext = buildContext,
                onBackPressed = { backstack.push(OnboardingRoutes.PrivacyNoteScreen) }
            )

            OnboardingRoutes.StartWorkerScreen -> StartWorkerNode(
                buildContext = buildContext,
                onBackPressed = { backstack.push(OnboardingRoutes.PermissionScreen) }
            )
        }
    }
}