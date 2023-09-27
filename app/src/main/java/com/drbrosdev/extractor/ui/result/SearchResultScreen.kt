package com.drbrosdev.extractor.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.BackButton
import com.drbrosdev.extractor.ui.components.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.ExtractorImagePlaceholder
import com.drbrosdev.extractor.ui.components.SearchFilterSheet
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.isScrollingUp
import com.drbrosdev.extractor.util.shimmerBrush

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    onNavToDetail: (selectedIndex: Int) -> Unit,
    onNavBack: () -> Unit,
    state: SearchResultScreenState,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val gridState = rememberLazyGridState()

    BottomSheetScaffold(
        sheetContent = {
            SearchFilterSheet(
                onClearFilterClick = {}
            )
        },
        sheetContainerColor = MaterialTheme.colorScheme.primary,
        sheetDragHandle = { Spacer(modifier.height(24.dp)) },
        sheetContentColor = Color.White,
        sheetPeekHeight = 100.dp,
        scaffoldState = scaffoldState
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .systemBarsPadding(),
        ) {
            val (backButton, images) = createRefs()
            val topLoadingGuideline = createGuidelineFromTop(0.10f)

            when (state) {
                SearchResultScreenState.Loading -> {
                    LoadingView(modifier = Modifier.constrainAs(images) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(topLoadingGuideline)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    })
                }

                is SearchResultScreenState.Success -> {
                    ExtractorImageGrid(
                        modifier = Modifier.constrainAs(
                            ref = images,
                            constrainBlock = {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                        ),
                        images = state.images,
                        searchTerm = state.searchTerm,
                        onClick = onNavToDetail,
                        gridState = gridState
                    )

                    AnimatedVisibility(
                        visible = gridState.isScrollingUp(),
                        modifier = Modifier.constrainAs(
                            ref = backButton,
                            constrainBlock = {
                                top.linkTo(parent.top, margin = 24.dp)
                                start.linkTo(parent.start)
                            }
                        ),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        BackButton(onClick = onNavBack)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingView(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        columns = GridCells.Fixed(count = 3),
    ) {
        item {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush())
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier.height(36.dp))
        }


        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(shimmerBrush())
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier.height(12.dp))
        }

        items(9) {
            ExtractorImagePlaceholder(size = 144)
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SearchScreenPreview() {
    ExtractorTheme(dynamicColor = false) {
        SearchResultScreen(
            state = SearchResultScreenState.Loading,
            onNavToDetail = {},
            onNavBack = {}
        )
    }
}
