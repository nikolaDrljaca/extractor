package com.drbrosdev.extractor.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButton
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmptySearch
import com.drbrosdev.extractor.ui.components.shared.ExtractorFirstSearch
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
    onStatusButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorSearchScreenUiState,
    dateFilterState: ExtractorDateFilterState,
    searchViewState: ExtractorSearchViewState,
    extractorStatusButtonState: ExtractorStatusButtonState,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                when (it) {
                    SheetValue.Hidden -> false
                    SheetValue.Expanded -> true
                    SheetValue.PartiallyExpanded -> true
                }
            }
        )
    )
    val gridState = rememberLazyGridState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (gridState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BottomSheetScaffold(
        sheetContent = {
            ExtractorSearchBottomSheet(
                onDone = onDone,
                searchViewState = searchViewState,
                dateFilterState = dateFilterState,
            )
        },
        sheetContainerColor = MaterialTheme.colorScheme.primary,
        sheetDragHandle = { Spacer(Modifier.height(20.dp)) },
        sheetContentColor = Color.White,
        sheetPeekHeight = 100.dp + bottomPadding,
        scaffoldState = scaffoldState
    ) {
        ConstraintLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize(),
            constraintSet = searchResultScreenConstraintSet()
        ) {
            AnimatedContent(
                targetState = state,
                label = "",
                modifier = Modifier.layoutId(ViewIds.MAIN_CONTENT)
            ) {
                when (it) {
                    ExtractorSearchScreenUiState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Loading")
                    }
                    is ExtractorSearchScreenUiState.FirstSearch -> ExtractorFirstSearch(modifier = Modifier.fillMaxSize())
                    is ExtractorSearchScreenUiState.Empty -> ExtractorEmptySearch()

                    is ExtractorSearchScreenUiState.Success ->
                        ExtractorImageGrid(
                            images = it.images,
                            onClick = onNavToDetail,
                            gridState = gridState,
                        )
                }
            }

            ExtractorTopBar(
                modifier = Modifier
                    .layoutId(ViewIds.TOP_BAR),
                state = extractorTopBarState.value,
                leadingSlot = {
                    ExtractorStatusButton(
                        onClick = onStatusButtonClick,
                        state = extractorStatusButtonState
                    )
                },
                centerSlot = { ExtractorHeader(bottomText = stringResource(R.string.tap_right_for_more)) },
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
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
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
    const val MAIN_CONTENT = "content"
    const val TOP_BAR = "topBar"
}