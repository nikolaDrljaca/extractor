package com.drbrosdev.extractor.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextButton

@Composable
fun ExtractorOnboardingScreen(
    onClick: () -> Unit,
    onBackClick: () -> Unit,
    pagerState: PagerState
) {

    val showBackButton = remember {
        derivedStateOf {
            pagerState.currentPage != 0
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        constraintSet = onboardingScreenConstraints()
    ) {
        HorizontalPager(
            modifier = Modifier.layoutId(ViewIds.PAGER),
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically,
            pageSize = PageSize.Fill,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> WelcomeCard()
                1 -> PrivacyNoteCard()
                2 -> PermissionCard()
                3 -> StartWorkerCard()
                else -> {}
            }
        }

        Box(
            modifier = Modifier.layoutId(ViewIds.BAR),
        ) {
            AnimatedVisibility(
                visible = showBackButton.value,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                ExtractorTextButton(onClick = onBackClick) {
                    Text(text = "Back")
                }
            }

            ExtractorButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                AnimatedContent(
                    targetState = pagerState.currentPage,
                    label = "",
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) {
                    when (it) {
                        2 -> Text(text = stringResource(id = R.string.grant_permission))
                        3 -> Text(text = stringResource(id = R.string.let_s_get_started))
                        else -> Text(text = "Next")
                    }
                }
            }
        }
    }
}


private fun onboardingScreenConstraints() = ConstraintSet {
    val pager = createRefFor(ViewIds.PAGER)
    val bar = createRefFor(ViewIds.BAR)

    val bottomGuideline = createGuidelineFromBottom(0.1f)

    constrain(pager) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        top.linkTo(parent.top)
        bottom.linkTo(bottomGuideline)
    }

    constrain(bar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
        top.linkTo(bottomGuideline)
        bottom.linkTo(parent.bottom)
    }
}

private object ViewIds {
    const val PAGER = "pager"
    const val BAR = "bar"
}
