package com.drbrosdev.extractor.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGrid
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButton
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheet
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetState
import com.drbrosdev.extractor.ui.components.searchsheet.SheetContent
import com.drbrosdev.extractor.ui.components.searchsheet.rememberExtractorSearchBottomSheetState
import com.drbrosdev.extractor.ui.components.shared.DragHandle
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmptySearch
import com.drbrosdev.extractor.ui.components.shared.ExtractorGetMoreSearches
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchFabStack
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorStillIndexing
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.suggestsearch.ExtractorSuggestedSearch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorSearchScreen(
    onExtractorHomeClicked: () -> Unit,
    onStatusButtonClick: () -> Unit,
    onMultiselectAction: (MultiselectAction) -> Unit,
    onHeaderClick: () -> Unit,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberExtractorSearchBottomSheetState()
    ),
    state: ExtractorSearchContainerState,
    searchCount: Int,
    extractorStatusButtonState: ExtractorStatusButtonState,
    snackbarHostState: SnackbarHostState,
    searchSheetState: ExtractorSearchSheetState,
) {
    val extractorTopBarState =
        when (state) {
            is ExtractorSearchContainerState.Content -> state.topAppBarState.value
            else -> ExtractorTopBarState.NORMAL
        }

    val sheetContent =
        when (state) {
            is ExtractorSearchContainerState.Content -> state.sheetContent.value
            else -> SheetContent.SearchView
        }

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BottomSheetScaffold(
        sheetContent = {
            AnimatedContent(targetState = sheetContent, label = "") {
                when (it) {
                    SheetContent.MultiselectBar -> ExtractorMultiselectActionBar(
                        onAction = onMultiselectAction,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = bottomPadding + 4.dp
                        )
                    )

                    SheetContent.SearchView -> ExtractorSearchSheet(
                        state = searchSheetState,
                        modifier = Modifier.padding(
                            bottom = bottomPadding + 4.dp
                        ),
                        isHidden = scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded
                    )
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        sheetContainerColor = MaterialTheme.colorScheme.primary,
        sheetDragHandle = { DragHandle(color = MaterialTheme.colorScheme.onPrimary) },
        sheetContentColor = MaterialTheme.colorScheme.onPrimary,
        sheetPeekHeight = 96.dp + bottomPadding,
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
                modifier = Modifier
                    .layoutId(ViewIds.MAIN_CONTENT)
            ) {
                when (it) {
                    ExtractorSearchContainerState.Loading -> Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            trackColor = Color.Transparent,
                            color = MaterialTheme.colorScheme.onBackground,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    is ExtractorSearchContainerState.StillIndexing -> ExtractorStillIndexing(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                    )

                    is ExtractorSearchContainerState.Empty -> ExtractorEmptySearch(
                        onReset = { it.onReset() }
                    )

                    is ExtractorSearchContainerState.Content -> Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        ExtractorImageGrid(
                            modifier = Modifier.fillMaxSize(),
                            images = it.images,
                            onClick = { index ->
                                it.eventSink(
                                    ExtractorSearchContainerEvents.OnImageClick(
                                        index
                                    )
                                )
                            },
                            gridState = it.gridState,
                            contentPadding = PaddingValues(0.dp)
                        )
                        ExtractorSearchFabStack(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 32.dp),
                            onAddClick = { it.eventSink(ExtractorSearchContainerEvents.OnCreateAlbumClick(it.images)) },
                            onResetClick = { it.eventSink(ExtractorSearchContainerEvents.OnReset) }
                        )
                    }

                    is ExtractorSearchContainerState.ShowSuggestions -> Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ExtractorSuggestedSearch(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            state = it.suggestedSearchState
                        )
                    }

                    is ExtractorSearchContainerState.NoSearchesLeft ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ExtractorGetMoreSearches(onClick = { it.onGetMore() })
                        }
                }
            }

            ExtractorTopBar(
                modifier = Modifier
                    .layoutId(ViewIds.TOP_BAR),
                state = extractorTopBarState,
                leadingSlot = {
                    ExtractorStatusButton(
                        onClick = onStatusButtonClick,
                        state = extractorStatusButtonState
                    )
                },
                centerSlot = {
                    ExtractorHeader(
                        bottomText = stringResource(R.string.searches_left, searchCount),
                        onClick = onHeaderClick
                    )
                },
                trailingSlot = {
                    IconButton(onClick = onExtractorHomeClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.extractor_icon),
                            contentDescription = "Go Home",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.layoutId(ViewIds.SNACKBAR),
                snackbar = { ExtractorSnackbar(snackbarData = it) }
            )
        }
    }
}

private fun searchResultScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)
    val snackbar = createRefFor(ViewIds.SNACKBAR)

    val bottomSheetGuideline = createGuidelineFromBottom(offset = 88.dp)

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom)
        bottom.linkTo(bottomSheetGuideline)
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
    }

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }

    constrain(snackbar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(bottomSheetGuideline, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val MAIN_CONTENT = "content"
    const val TOP_BAR = "topBar"
    const val SNACKBAR = "snack_bar"
}