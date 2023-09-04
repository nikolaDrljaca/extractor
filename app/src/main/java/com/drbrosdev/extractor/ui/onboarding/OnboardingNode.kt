package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
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
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerOnbCard
import com.drbrosdev.extractor.ui.theme.md_theme_light_secondary
import com.drbrosdev.extractor.ui.theme.md_theme_light_tertiary
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.SlideTransitionSpec
import com.drbrosdev.extractor.util.applicationIconBitmap
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.rememberNavController
import kotlinx.parcelize.Parcelize


@Parcelize
object Onboarding : NavTarget {

    @Composable
    override fun Content() {
        OnboardingScreen()
    }
}

interface OnbNavTarget : NavTarget {
    @Composable
    override fun Content()
}

val LocalOnbNavController = staticCompositionLocalOf<NavController<OnbNavTarget>> {
    error("NavController not provided in this scope.")
}

@Composable
private fun OnboardingScreen(
    modifier: Modifier = Modifier,
) {
    val onbNavController = rememberNavController(
        initialBackstack = listOf(
            StartWorkerOnbCard, PermissionOnbCard, PrivacyNoteOnbCard, WelcomeOnbCard
        )
    )

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
        val cardGuideline = createGuidelineFromBottom(0.45f)
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
            Image(bitmap = applicationIconBitmap(), contentDescription = "")
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

        AnimatedNavHost(
            controller = onbNavController,
            transitionSpec = SlideTransitionSpec,
            modifier = modifier.constrainAs(cards) {
                top.linkTo(cardGuideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 12.dp)
                height = Dimension.fillToConstraints
            }
        ) {
            CompositionLocalProvider(
                LocalOnbNavController provides onbNavController
            ) {
                it.Content()
            }
        }
    }
}