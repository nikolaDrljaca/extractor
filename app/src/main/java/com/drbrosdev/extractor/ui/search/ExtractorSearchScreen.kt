package com.drbrosdev.extractor.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponent
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultContentEvents
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheet
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmptySearch
import com.drbrosdev.extractor.ui.components.shared.ExtractorGetMoreSearches
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.util.maxLineSpanItem

@Composable
fun ExtractorSearchScreen(
    onBack: () -> Unit,
    sheetComponent: ExtractorSearchSheetComponent,
    resultComponent: SearchResultComponent,
    snackbarHostState: SnackbarHostState,
) {
    val resultState = resultComponent.state.value
    val imageSize = 96

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = searchResultScreenConstraintSet()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .layoutId(ViewIds.MAIN_CONTENT),
            columns = GridCells.Adaptive(minSize = imageSize.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            state = resultComponent.gridState.lazyGridState
        ) {
            maxLineSpanItem(key = "scroll_marker") { Spacer(Modifier.height(1.dp)) }
            // Top Bar
            maxLineSpanItem(key = "top_bar") {
                ExtractorTopBar(
                    paddingValues = PaddingValues(),
                    leadingSlot = {
                        BackIconButton(onBack = onBack)
                    },
                    trailingSlot = {
                        if (resultComponent.canCreateAlbum) {
                            TextButton(onClick = resultComponent::saveAsAlbum) {
                                Text(text = "Save as album")
                            }
                        }
                    }
                )
            }
            // search sheet
            maxLineSpanItem(key = "search_sheet") {
                ExtractorSearchSheet(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    component = sheetComponent,
                )
            }
            maxLineSpanItem(key = "result_marker") {
                Spacer(Modifier.height(8.dp))
            }
            // search results -- content
            when (resultState) {
                SearchResultState.Idle -> Unit

                SearchResultState.Empty -> maxLineSpanItem(key = "empty") {
                    ExtractorEmptySearch(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .animateItem()
                    )
                }

                is SearchResultState.NoSearchesLeft -> maxLineSpanItem(key = "noSearchesLeft") {
                    ExtractorGetMoreSearches(
                        onClick = resultState.onGetMore,
                        modifier = Modifier.animateItem()
                    )
                }

                is SearchResultState.Content -> {
                    items(
                        items = resultState.images,
                        key = { it.mediaImageId.id }
                    ) {
                        ExtractorImageItem(
                            imageUri = it.uri.toUri(),
                            modifier = Modifier
                                .animateItem(),
                            size = imageSize,
                            onClick = {
                                resultState.eventSink(SearchResultContentEvents.OnImageClick(it))
                            },
                            onLongClick = {
                                resultState.eventSink(
                                    SearchResultContentEvents.OnLongImageTap(
                                        it
                                    )
                                )
                            },
                            checkedState = resultComponent.gridState[it.mediaImageId]
                        )
                    }
                    // TODO Modify value based on screen size
                    maxLineSpanItem(key = "bottom_spacer") { Spacer(Modifier.height(100.dp)) }
                }
            }
        }

        AnimatedVisibility(
            visible = resultComponent.isScrollToTopVisible,
            modifier = Modifier
                .layoutId(ViewIds.FAB),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                onClick = resultComponent::focusSearchField,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Back to top"
                )
            }
        }

        AnimatedVisibility(
            visible = resultComponent.multiselectActionBarVisible,
            modifier = Modifier
                .layoutId(ViewIds.ACTION_BAR),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorMultiselectActionBar(
                onAction = resultComponent::multiselectEventHandler
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .layoutId(ViewIds.SNACKBAR)
        ) {
            ExtractorSnackbar(it)
        }
    }
}

private fun searchResultScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)
    val snackbar = createRefFor(ViewIds.SNACKBAR)
    val actionBar = createRefFor(ViewIds.ACTION_BAR)
    val fab = createRefFor(ViewIds.FAB)

    val bottomSheetGuideline = createGuidelineFromBottom(offset = 88.dp)

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
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

    constrain(actionBar) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        bottom.linkTo(parent.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(fab) {
        end.linkTo(parent.end, margin = 32.dp)
        bottom.linkTo(parent.bottom, margin = 32.dp)
    }
}

private object ViewIds {
    const val MAIN_CONTENT = "content"
    const val TOP_BAR = "topBar"
    const val SNACKBAR = "snack_bar"
    const val ACTION_BAR = "search_multi_bar"
    const val FAB = "fab_view"
}