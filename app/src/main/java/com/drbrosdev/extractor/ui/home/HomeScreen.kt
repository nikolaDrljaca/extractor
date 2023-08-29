package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.ui.components.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.SearchBar
import com.drbrosdev.extractor.ui.components.SearchTopBar
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeScreenEvents) -> Unit,
    onNavToImageNode: (List<MediaImage>, Int) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
    ) {
        val topGuideline = createGuidelineFromTop(0.15f)
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

        SearchTopBar(
            modifier = Modifier
                .constrainAs(
                    ref = status,
                    constrainBlock = {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                ),
            onClick = { onEvent(HomeScreenEvents.RunExtraction) },
            onAboutClick = {},
        )

        if (state.images.isNotEmpty()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .constrainAs(
                        ref = previousSearch,
                        constrainBlock = {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(searchBar.bottom, margin = 8.dp)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                    ),
                columns = GridCells.Fixed(count = 3),
            ) {
                itemsIndexed(state.images, key = { _, it -> it.id }) { index, it ->
                    ExtractorImageItem(
                        imageUri = it.uri,
                        size = 144,
                        onClick = {
                            onNavToImageNode(state.images, index)
                        }
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        } else {
            //TODO: Need multiple states -> Recent searches, empty result, loading
            LazyColumn(
                modifier = Modifier
                    .constrainAs(
                        ref = previousSearch,
                        constrainBlock = {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(searchBar.bottom)
                        }
                    )
            ) {
//            items(temp) {
//                PreviousSearchItem(
//                    modifier = Modifier.padding(start = 4.dp),
//                    text = it,
//                    onClick = { /*TODO*/ },
//                    onDelete = {}
//                )
//            }
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
    ExtractorTheme {
        Surface {
            HomeScreen(
                state = HomeUiState(),
                onEvent = {},
                onNavToImageNode = { uri, int -> }
            )
        }
    }
}
