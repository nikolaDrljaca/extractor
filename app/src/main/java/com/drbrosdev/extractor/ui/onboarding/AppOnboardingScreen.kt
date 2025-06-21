package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.theme.seedColor

@Composable
fun AppOnboardingScreen(
    onClick: () -> Unit,
    pagerState: PagerState
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            pagerState.currentPage != 3 -> seedColor
            else -> MaterialTheme.colorScheme.background
        },
        label = ""
    )

    ConstraintLayout(
        modifier = Modifier
            .background(backgroundColor)
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = onboardingScreenConstraints()
    ) {
        HorizontalPager(
            modifier = Modifier.layoutId(ViewIds.PAGER),
            state = pagerState,
            verticalAlignment = Alignment.Bottom,
            pageSize = PageSize.Fill,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                0 -> WelcomeCard(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                    onGetStarted = onClick
                )

                1 -> PrivacyNote(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                    onNext = onClick
                )

                2 -> PermissionCard(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                    onPermission = onClick
                )
                3 -> StartWorkerCard(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                    onStart = onClick
                )
                else -> {}
            }
        }
    }
}


private fun onboardingScreenConstraints() = ConstraintSet {
    val pager = createRefFor(ViewIds.PAGER)
    val bar = createRefFor(ViewIds.BAR)

    constrain(pager) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
    }

    constrain(bar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        top.linkTo(parent.top, margin = 24.dp)
    }
}

private object ViewIds {
    const val PAGER = "pager"
    const val BAR = "bar"
}
