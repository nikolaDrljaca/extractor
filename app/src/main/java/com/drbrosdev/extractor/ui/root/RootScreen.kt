package com.drbrosdev.extractor.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.drbrosdev.extractor.ui.home.HomeNavTarget
import com.drbrosdev.extractor.ui.onboarding.Onboarding
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.SlideTransitionSpec
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.koinViewModel


@Composable
fun Root(
    modifier: Modifier = Modifier,
) {
    val viewModel: RootViewModel = koinViewModel()
    val navController = rememberNavController<NavTarget>(startDestination = HomeNavTarget)

    LaunchedEffect(key1 = Unit) {
        val isOnboardingFinished = viewModel.isOnboardingFinished().first()
        if (isOnboardingFinished)
            return@LaunchedEffect
        else
            navController.replaceAll(Onboarding)
    }

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

