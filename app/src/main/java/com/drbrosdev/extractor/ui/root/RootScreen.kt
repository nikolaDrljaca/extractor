package com.drbrosdev.extractor.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.drbrosdev.extractor.ui.home.HomeNavTarget
import com.drbrosdev.extractor.ui.onboarding.Onboarding
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.SlideTransitionSpec
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.rememberNavController
import org.koin.androidx.compose.koinViewModel


@Composable
fun Root(
    modifier: Modifier = Modifier,
) {
    val viewModel: RootViewModel = koinViewModel()
    val isOnboardingFinished by viewModel.isOnboardingFinished.collectAsState()
    val navTarget = if (isOnboardingFinished) HomeNavTarget else Onboarding

    val navController = rememberNavController(startDestination = navTarget)

    NavBackHandler(controller = navController)

    AnimatedNavHost(
        controller = navController,
        transitionSpec = SlideTransitionSpec,
    ) {
        CompositionLocalProvider(LocalNavController provides navController) {
            it.Content()
        }
    }
}

