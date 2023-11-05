package com.drbrosdev.extractor.ui.result

import android.content.res.Configuration
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.datafilterchip.ImageLabelFilterChipData
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.shared.QueryTextHeader
import com.drbrosdev.extractor.ui.components.shared.QueryTextHeaderState
import com.drbrosdev.extractor.ui.components.shared.SearchFilterSheet
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    onNavToDetail: (selectedIndex: Int) -> Unit,
    onFilterChanged: (ImageLabelFilterChipData) -> Unit,
    modifier: Modifier = Modifier,
    state: SearchResultUiState,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val gridState = rememberLazyGridState()
    val queryTextState = remember {
        derivedStateOf {
            if (gridState.firstVisibleItemIndex > 0) QueryTextHeaderState.ELEVATED
            else QueryTextHeaderState.NORMAL
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            SearchFilterSheet(
                onFilterChanged = onFilterChanged,
                onClearFilterClick = {},
                initialLabelSelected = state.initialLabelIndex
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
            constraintSet = searchResultScreenConstraintSet()
        ) {
            when (state) {
                is SearchResultUiState.Loading -> {
                    LoadingView(modifier = Modifier.layoutId(ViewIds.SPINNER))
                }

                is SearchResultUiState.Success -> {
                    ExtractorImageGrid(
                        modifier = Modifier.layoutId(ViewIds.IMAGE_GRID),
                        images = state.images,
                        onClick = onNavToDetail,
                        gridState = gridState,
                    )
                }
            }

            QueryTextHeader(
                text = state.searchTerm,
                modifier = Modifier.layoutId(ViewIds.QUERY),
                state = queryTextState.value
            )
        }
    }
}

private fun searchResultScreenConstraintSet() = ConstraintSet {
    val spinner = createRefFor(ViewIds.SPINNER)
    val imageGrid = createRefFor(ViewIds.IMAGE_GRID)
    val queryText = createRefFor(ViewIds.QUERY)

    constrain(spinner) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top, margin = 12.dp)
        width = Dimension.fillToConstraints
    }

    constrain(queryText) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top, margin = 24.dp)
        width = Dimension.fillToConstraints
    }

    constrain(imageGrid) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }
}

private object ViewIds {
    //backButton, queryText, imageGrid, loading
    const val BACK = "BACK"
    const val QUERY = "QUERY"
    const val IMAGE_GRID = "imageGrid"
    const val SPINNER = "loading"

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
            onFilterChanged = {},
            state = SearchResultUiState.Loading(
                searchTerm = "",
                labelType = LabelType.ALL
            ),
            onNavToDetail = {},
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SearchScreenPreviewDark() {
    ExtractorTheme(dynamicColor = false) {
        SearchResultScreen(
            onFilterChanged = {},
            state = SearchResultUiState.Loading(
                searchTerm = "",
                labelType = LabelType.ALL
            ),
            onNavToDetail = {},
        )
    }
}
