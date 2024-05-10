package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.permission.PermissionService
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import dev.olshevski.navigation.reimagined.replaceAll
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorOnboardingNavTarget : NavTarget {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: OnboardingViewModel = koinViewModel()
        val pagerState = rememberPagerState {
            4
        }
        val scope = rememberCoroutineScope()
        val navController = LocalNavController.current

        val permissionRequest = PermissionService.requestPermissions {
            if (it) {
                scope.launch {
                    pagerState.animateScrollToPage(3)
                }
            }
        }

        ExtractorOnboardingScreen(
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
                        navController.replaceAll(ExtractorSearchNavTarget)
                    }
                }
            },
            onBackClick = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }
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


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorOnboardingScreen(
                pagerState = rememberPagerState { 0 },
                onClick = {},
                onBackClick = {}
            )
        }
    }
}