package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ImageSearchParams
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.usecase.image.SearchImages
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorListItemCheckedState
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheet
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.maxLineSpanItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
sealed interface SearchResultState {
    data class NoSearchesLeft(
        val onGetMore: () -> Unit
    ) : SearchResultState

    // result list is empty
    data object Empty : SearchResultState

    // no search was executed yet -- initial state
    data object Idle : SearchResultState

    @Immutable
    data class Content(
        val images: List<Extraction>,
        val eventSink: (SearchResultContentEvents) -> Unit,
    ) : SearchResultState
}

sealed interface SearchResultContentEvents {
    data class OnImageClick(val index: Int) : SearchResultContentEvents

    data class OnCreateAlbumClick(val images: List<Extraction>) : SearchResultContentEvents
}

@Stable
class SearchResultComponent(
    private val coroutineScope: CoroutineScope,
    private val searchImages: SearchImages,
) {
    private val _searchTrigger = MutableSharedFlow<ImageSearchParams?>()

    val state = _searchTrigger
        .map { runImageSearch(it) }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            SearchResultState.Idle
        )

    fun executeSearch(params: ImageSearchParams?) {
        coroutineScope.launch { _searchTrigger.emit(params) }
    }

    private suspend fun runImageSearch(params: ImageSearchParams?) =
        when {
            params != null -> searchImages.execute(params).fold(
                ifLeft = {
                    SearchResultState.NoSearchesLeft(
                        onGetMore = {}
                    )
                },
                ifRight = {
                    when {
                        it.isEmpty() -> SearchResultState.Empty

                        else -> SearchResultState.Content(
                            images = it,
                            eventSink = ::searchContentStateEventHandler
                        )
                    }
                }
            )

            else -> SearchResultState.Idle
        }

    private fun searchContentStateEventHandler(event: SearchResultContentEvents) {

    }
}

@Composable
fun ExtractorSearchScreen(
    searchSheetComponent: ExtractorSearchSheetComponent,
    searchResultState: SearchResultState
) {
    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = searchResultScreenConstraintSet()
    ) {
        LazyVerticalGrid(
            modifier = Modifier.layoutId(ViewIds.MAIN_CONTENT),
            columns = GridCells.Adaptive(minSize = 96.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // search sheet
            maxLineSpanItem {
                ExtractorSearchSheet(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    component = searchSheetComponent
                )
            }
            // search results -- content
            when (searchResultState) {
                SearchResultState.Empty -> Unit
                SearchResultState.Idle -> Unit
                is SearchResultState.NoSearchesLeft -> Unit

                is SearchResultState.Content -> items(searchResultState.images) {
                    ExtractorImageItem(
                        imageUri = it.uri.toUri(),
                        modifier = Modifier
                            .animateItem(),
                        size = 96,
                        onClick = {},
                        onLongClick = {},
                        checkedState = ExtractorListItemCheckedState.UNCHECKED
                    )
                }
            }
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
}

private object ViewIds {
    const val MAIN_CONTENT = "content"
    const val TOP_BAR = "topBar"
    const val SNACKBAR = "snack_bar"
}