package com.drbrosdev.extractor.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.framework.ShowSystemBarsEffect
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultComponent
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultContentEvents
import com.drbrosdev.extractor.ui.components.searchresult.SearchResultState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorEmptySearch
import com.drbrosdev.extractor.ui.components.shared.ExtractorGetMoreSearches
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchTextField
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.util.isScrollingUp
import com.drbrosdev.extractor.util.maxLineSpanItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LupaSearchScreen(
    onBack: () -> Unit,
    sheetComponent: ExtractorSearchSheetComponent,
    resultComponent: SearchResultComponent,
    snackbarHostState: SnackbarHostState,
) {
    val resultState = resultComponent.state.value
    val imageSize = 96

    // This should be somewhere else
    val modalSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    ShowSystemBarsEffect()

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = imageSize.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            state = resultComponent.gridState.lazyGridState
        ) {
            maxLineSpanItem(key = "scroll_marker") { Spacer(Modifier.height(16.dp)) }
            // Top Bar
            maxLineSpanItem(key = "top_bar") {
                ExtractorTopBar(
                    paddingValues = PaddingValues(),
                    leadingSlot = {
                        BackIconButton(onBack = onBack)
                    },
                )
            }
            maxLineSpanItem {
                ExtractorSearchTextField(
                    textFieldState = sheetComponent.query,
                    onDoneSubmit = sheetComponent::onSearch,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .focusRequester(sheetComponent.focusRequester)
                        .fillMaxWidth(),
                )
            }
            // search sheet
            maxLineSpanItem(key = "result_marker") {
                Spacer(Modifier.height(16.dp))
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
                    maxLineSpanItem {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Results",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
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
                    maxLineSpanItem(key = "bottom_spacer") { Spacer(Modifier.height(100.dp)) }
                }
            }
        }

        LupaSearchFloatingBar(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = -FloatingToolbarDefaults.ScreenOffset,
                    y = -FloatingToolbarDefaults.ScreenOffset
                ),
            expanded = resultComponent.gridState.lazyGridState.isScrollingUp(),
            onEvent = {
                // TODO This should be somewhere else
                when (it) {
                    LupaSearchFloatingBarEvent.OnAdd -> resultComponent.saveAsAlbum()
                    LupaSearchFloatingBarEvent.OnFilter -> showBottomSheet = true
                    LupaSearchFloatingBarEvent.OnSearch -> resultComponent.focusSearchField()
                    LupaSearchFloatingBarEvent.OnReset -> {
                        sheetComponent.clearState()
                        resultComponent.clearState()
                        resultComponent.focusSearchField()
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = resultComponent.multiselectActionBarVisible,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
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
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 2.dp)
        ) {
            ExtractorSnackbar(it)
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = modalSheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                LupaFilterSheet(
                    component = sheetComponent
                )
            }
        }
    }
}