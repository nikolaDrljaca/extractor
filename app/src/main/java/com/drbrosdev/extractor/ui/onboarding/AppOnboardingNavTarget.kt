package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.framework.permission.requestExtractionPermissions
import com.drbrosdev.extractor.ui.overview.ExtractorOverviewNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object AppOnboardingNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: OnboardingViewModel = koinViewModel()
        val pagerState = rememberPagerState {
            4
        }
        val scope = rememberCoroutineScope()
        val navController = navigators.navController

        val permissionRequest = requestExtractionPermissions {
            if (it) {
                scope.launch {
                    pagerState.animateScrollToPage(3)
                }
            }
        }

        AppOnboardingScreen(
            pagerState = pagerState,
            onClick = {
                val event = mapEvent(pagerState.currentPage)
                when (event) {
                    OnboardingEvents.GoNext -> {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }

                    OnboardingEvents.RequestPermissions -> {
                        permissionRequest.invoke()
                    }

                    OnboardingEvents.StartWorker -> {
                        viewModel.finishOnboarding()
                        navController.replaceAll(ExtractorOverviewNavTarget)
                    }
                }
            },
        )
    }
}

sealed interface OnboardingEvents {
    data object RequestPermissions : OnboardingEvents
    data object GoNext : OnboardingEvents
    data object StartWorker : OnboardingEvents
}

private fun mapEvent(currentPage: Int): OnboardingEvents = when (currentPage) {
    2 -> OnboardingEvents.RequestPermissions
    3 -> OnboardingEvents.StartWorker
    else -> OnboardingEvents.GoNext
}


@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppOnboardingScreen(
                pagerState = rememberPagerState { 0 },
                onClick = {},
            )
        }
    }
}