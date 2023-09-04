package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.HomeTopBar
import com.drbrosdev.extractor.ui.components.PreviousSearchItem
import com.drbrosdev.extractor.ui.components.SearchBar
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeScreenEvents) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
    ) {
        val topGuideline = createGuidelineFromTop(0.2f)
        val (searchBar, previousSearch, status, aboutIcon) = createRefs()

        SearchBar(
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
            onDone = { onEvent(HomeScreenEvents.PerformSearch(it)) }
        )

        HomeTopBar(
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
            onClick = { },
            onAboutClick = { },
        )

        //TODO: Need multiple states -> Recent searches, empty
        LazyColumn(
            modifier = Modifier
                .constrainAs(
                    ref = previousSearch,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(searchBar.bottom, margin = 24.dp)
                    }
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                Text(
                    text = "Previous Searches",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            items(state.searches) {
                PreviousSearchItem(
                    modifier = Modifier.animateItemPlacement(),
                    text = it.query,
                    count = it.resultCount,
                    onClick = { onEvent(HomeScreenEvents.PerformSearch(it.query)) },
                    onDelete = { onEvent(HomeScreenEvents.OnDeleteSearch(it)) }
                )
            }
        }
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
                state = HomeUiState(),
                onEvent = {},
            )
        }
    }
}
