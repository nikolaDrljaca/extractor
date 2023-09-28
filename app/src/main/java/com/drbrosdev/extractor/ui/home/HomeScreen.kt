package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewEvents
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearches
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarEvents
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun HomeScreen(
    onTopBarEvent: (ExtractorTopBarEvents) -> Unit,
    onSearchViewEvents: (ExtractorSearchViewEvents) -> Unit,
    onPreviousSearchEvents: (PreviousSearchesEvents) -> Unit,
    donePercentage: Int?,
    previousSearches: List<PreviousSearchItemState>,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
    ) {
        val topGuideline = createGuidelineFromTop(0.16f)
        val (searchBar, previousSearch, status, aboutIcon) = createRefs()

        ExtractorSearchView(
            modifier = Modifier
                .constrainAs(
                    ref = searchBar,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(topGuideline)
                        width = Dimension.fillToConstraints
                    }
                ),
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

        ExtractorTopBar(
            modifier = Modifier
                .constrainAs(
                    ref = status,
                    constrainBlock = {
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                ),
            donePercentage = donePercentage,
            onEvent = onTopBarEvent
        )

        PreviousSearches(
            modifier = Modifier
                .constrainAs(
                    ref = previousSearch,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(searchBar.bottom, margin = 24.dp)
                    }
                ),
            onEvent = onPreviousSearchEvents,
            searches = previousSearches
        )
    }
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
                donePercentage = null,
                previousSearches = emptyList()
            )
        }
    }
}