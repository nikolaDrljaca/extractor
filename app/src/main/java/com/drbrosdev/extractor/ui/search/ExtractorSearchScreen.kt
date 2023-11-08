package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButton
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchBottomSheet
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorSearchScreen(
    onNavToDetail: (selectedIndex: Int) -> Unit,
    onExtractorHomeClicked: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorSearchScreenUiState,
    searchViewState: ExtractorSearchViewState,
    extractorStatusButtonState: ExtractorStatusButtonState,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val gridState = rememberLazyGridState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (gridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            ExtractorSearchBottomSheet(
                onDone = onDone,
                searchViewState = searchViewState
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
                .fillMaxSize(),
            constraintSet = searchResultScreenConstraintSet()
        ) {
            when (state) {
                is ExtractorSearchScreenUiState.Loading -> {
                    LoadingView(modifier = Modifier.layoutId(ViewIds.SPINNER))
                }

                is ExtractorSearchScreenUiState.Success -> {
                    ExtractorImageGrid(
                        modifier = Modifier.layoutId(ViewIds.IMAGE_GRID),
                        images = state.images,
                        onClick = onNavToDetail,
                        gridState = gridState,
                    )
                }
            }

            ExtractorTopBar(
                modifier = Modifier.layoutId(ViewIds.TOP_BAR),
                state = extractorTopBarState.value,
                leadingSlot = {
                    ExtractorStatusButton(
                        onClick = {},
                        state = extractorStatusButtonState
                    )
                },
                centerSlot = { ExtractorHeader() },
                trailingSlot = {
                    IconButton(onClick = onExtractorHomeClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_android_24),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            )
        }
    }
}

private fun searchResultScreenConstraintSet() = ConstraintSet {
    val spinner = createRefFor(ViewIds.SPINNER)
    val imageGrid = createRefFor(ViewIds.IMAGE_GRID)
    val topBar = createRefFor(ViewIds.TOP_BAR)

    constrain(spinner) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(topBar.bottom, margin = 4.dp)
        width = Dimension.fillToConstraints
    }

    constrain(imageGrid) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val IMAGE_GRID = "imageGrid"
    const val SPINNER = "loading"
    const val TOP_BAR = "topBar"
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