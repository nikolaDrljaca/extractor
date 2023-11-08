package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButton
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearches
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.stats.ExtractorStats
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState

@Composable
fun ExtractorHomeScreen(
    searchViewState: ExtractorSearchViewState,
    statsUiState: ExtractorStatsUiState,
    extractorStatusButtonState: ExtractorStatusButtonState,
    previousSearches: List<PreviousSearchItemState>,
    onStatusButtonClick: () -> Unit,
    onSearchViewDone: () -> Unit,
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
        ExtractorSearchView(
            onDone = onSearchViewDone,
            state = searchViewState,
            modifier = Modifier.layoutId(ViewIds.SEARCH_VIEW)
        )

        ExtractorStats(
            modifier = Modifier.layoutId(ViewIds.STATS),
            state = statsUiState,
            onStatClick = onStatClick
        )

        ExtractorTopBar(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            leadingSlot = {
                ExtractorStatusButton(
                    onClick = onStatusButtonClick,
                    state = extractorStatusButtonState
                )
            },
            centerSlot = { ExtractorHeader() },
        )

        PreviousSearches(
            modifier = Modifier.layoutId(ViewIds.PREV_SEARCH),
            onEvent = onPreviousSearchEvents,
            searches = previousSearches
        )
    }
}

private fun homeScreenConstraintSet() = ConstraintSet {
    val topGuideline = createGuidelineFromTop(0.15f)
    val searchView = createRefFor(ViewIds.SEARCH_VIEW)
    val previousSearch = createRefFor(ViewIds.PREV_SEARCH)
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val stats = createRefFor(ViewIds.STATS)

    constrain(searchView) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topGuideline)
        width = Dimension.fillToConstraints
    }

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
        top.linkTo(searchView.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val SEARCH_VIEW = "searchView"
    const val PREV_SEARCH = "prevSearch"
    const val TOP_BAR = "topBar"
    const val STATS = "stats"
}

