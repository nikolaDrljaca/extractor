package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearches
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.stats.ExtractorStats
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarEvents
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun HomeScreen(
    onTopBarEvent: (ExtractorTopBarEvents) -> Unit,
    onSearchViewEvents: (ExtractorSearchViewEvents) -> Unit,
    onPreviousSearchEvents: (PreviousSearchesEvents) -> Unit,
    onStatClick: (String, LabelType) -> Unit,
    donePercentage: Int?,
    previousSearches: List<PreviousSearchItemState>,
    statsUiState: ExtractorStatsUiState
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
            modifier = Modifier.layoutId(ViewIds.SEARCH_VIEW),
            onDone = { onSearchViewEvents(ExtractorSearchViewEvents.OnPerformSearch) },
            onFilterChanged = {
                onSearchViewEvents(
                    ExtractorSearchViewEvents.OnImageLabelFilterChanged(
                        it
                    )
                )
            },
            onQueryChanged = { onSearchViewEvents(ExtractorSearchViewEvents.OnQueryChanged(it)) }
        )

        ExtractorStats(
            modifier = Modifier.layoutId(ViewIds.STATS),
            state = statsUiState,
            onStatClick = onStatClick
        )

        ExtractorTopBar(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            donePercentage = donePercentage,
            onEvent = onTopBarEvent
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


@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            HomeScreen(
                onTopBarEvent = {},
                onSearchViewEvents = {},
                onPreviousSearchEvents = {},
                onStatClick = { query, type -> },
                donePercentage = null,
                previousSearches = emptyList(),
                statsUiState = ExtractorStatsUiState.Loading
            )
        }
    }
}