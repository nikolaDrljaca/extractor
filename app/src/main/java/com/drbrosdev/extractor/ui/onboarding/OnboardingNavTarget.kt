package com.drbrosdev.extractor.ui.onboarding

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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.animspec.CardStackSpec
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerOnbCard
import com.drbrosdev.extractor.util.applicationIconBitmap
import com.drbrosdev.extractor.util.createExtractorBrush
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

    val brush = createExtractorBrush()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
    ) {
        val topGuideline = createGuidelineFromTop(0.05f)
        val cardGuideline = createGuidelineFromBottom(0.55f)
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
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
        }

        AnimatedNavHost(
            controller = onbNavController,
            transitionSpec = CardStackSpec,
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