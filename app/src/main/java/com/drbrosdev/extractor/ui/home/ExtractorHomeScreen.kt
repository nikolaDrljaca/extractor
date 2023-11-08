package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearches
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.stats.ExtractorStats
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState

@Composable
fun ExtractorHomeScreen(
    statsUiState: ExtractorStatsUiState,
    previousSearches: List<PreviousSearchItemState>,
    onPreviousSearchEvents: (PreviousSearchesEvents) -> Unit,
    onStatClick: (String, LabelType) -> Unit,
) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
            .verticalScroll(scrollState),
        constraintSet = homeScreenConstraintSet()
    ) {
        ExtractorStats(
            modifier = Modifier.layoutId(ViewIds.STATS),
            state = statsUiState,
            onStatClick = onStatClick
        )

        Text(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            text = stringResource(R.string.extractor_home),
            style = MaterialTheme.typography.headlineMedium
        )

        PreviousSearches(
            modifier = Modifier.layoutId(ViewIds.PREV_SEARCH),
            onEvent = onPreviousSearchEvents,
            searches = previousSearches
        )
    }
}

private fun homeScreenConstraintSet() = ConstraintSet {
    val previousSearch = createRefFor(ViewIds.PREV_SEARCH)
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val stats = createRefFor(ViewIds.STATS)

    constrain(previousSearch) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(stats.bottom, margin = 16.dp)
    }

    constrain(topBar) {
        top.linkTo(parent.top, margin = 24.dp)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(stats) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val PREV_SEARCH = "prevSearch"
    const val TOP_BAR = "topBar"
    const val STATS = "stats"
}

