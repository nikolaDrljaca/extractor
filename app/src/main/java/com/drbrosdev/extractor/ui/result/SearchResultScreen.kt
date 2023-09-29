package com.drbrosdev.extractor.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.ui.components.BackButton
import com.drbrosdev.extractor.ui.components.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.SearchFilterSheet
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.isScrollingUp

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
                        top.linkTo(parent.top, margin = 12.dp)
                        width = Dimension.fillToConstraints
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
    LinearProgressIndicator(
        modifier = Modifier.then(modifier),
        color = MaterialTheme.colorScheme.primary,
        trackColor = Color.Transparent,
        strokeCap = StrokeCap.Round
    )
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
