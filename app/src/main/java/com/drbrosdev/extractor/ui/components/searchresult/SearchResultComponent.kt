package com.drbrosdev.extractor.ui.components.searchresult

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import arrow.core.Either
import com.drbrosdev.extractor.domain.model.LupaImage
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.search.ImageSearchParams
import com.drbrosdev.extractor.domain.model.search.isNotBlank
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.shop.ExtractorHubNavTarget
import com.drbrosdev.extractor.util.WhileUiSubscribed
import com.drbrosdev.extractor.util.asState
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class SearchResultComponent(
    private val coroutineScope: CoroutineScope,
    private val searchImages: suspend (ImageSearchParams) -> Either<Unit, List<LupaImage>>,
    private val createAlbum: (List<LupaImageMetadata>) -> Unit,
    private val navigators: Navigators
) {
    private val _events = Channel<SearchResultComponentEvents>()
    val event = _events.receiveAsFlow()

    private val _searchTrigger = MutableSharedFlow<ImageSearchParams?>()

    val gridState = ExtractorGridState<MediaImageId>()

    val multiselectActionBarVisible by derivedStateOf {
        gridState.checkedKeys().isNotEmpty()
    }

    val state = _searchTrigger
        .filter { it?.isNotBlank() ?: true }
        .map { runImageSearch(it) }
        .onEach {
            if (it is SearchResultState.Content) {
                _events.send(SearchResultComponentEvents.SearchComplete)
            }
        }
        .stateIn(
            coroutineScope,
            WhileUiSubscribed,
            SearchResultState.Idle
        )
        .asState(coroutineScope)

    val canCreateAlbum by derivedStateOf {
        val isContent = state.value is SearchResultState.Content
        if (!isContent) return@derivedStateOf false
        state.value.getImages().isNotEmpty()
    }

    fun executeSearch(params: ImageSearchParams?) {
        coroutineScope.launch {
            // trigger search
            _searchTrigger.emit(params)
        }
    }

    fun multiselectEventHandler(event: MultiselectAction) {
        when (event) {
            MultiselectAction.Delete -> Unit // Not supported by this grid
            MultiselectAction.Cancel -> gridState.clearSelection()

            MultiselectAction.CreateAlbum -> {
                val images = gridState.checkedKeys()
                    .mapNotNull { state.value.getImageLookup()[it] }
                createAlbum(images)
                gridState.clearSelection()
            }

            MultiselectAction.Share -> coroutineScope.launch {
                val uris = gridState.checkedKeys()
                    .mapNotNull { state.value.getImageLookup()[it] }
                    .map { it.uri.toUri() }
                _events.send(SearchResultComponentEvents.Share(uris))
                gridState.clearSelection()
            }
        }
    }

    fun focusSearchField() {
        coroutineScope.launch {
            // send event to scroll to top
            _events.send(SearchResultComponentEvents.ScrollToTop)
        }
    }

    fun saveAsAlbum() {
        if (canCreateAlbum.not()) return
        val images = state.value.getImages()
        createAlbum(images)
    }

    fun clearState() {
        coroutineScope.launch {
            _searchTrigger.emit(null)
        }
    }

    private suspend fun runImageSearch(params: ImageSearchParams?) =
        when {
            params != null -> searchImages(params).fold(
                ifLeft = {
                    SearchResultState.NoSearchesLeft(
                        onGetMore = {
                            navigators.navController.navigate(
                                ExtractorHubNavTarget
                            )
                        }
                    )
                },
                ifRight = {
                    when {
                        it.isEmpty() -> SearchResultState.Empty

                        else -> SearchResultState.Content(
                            images = it.map { o -> o.metadata },
                            eventSink = ::searchContentStateEventHandler
                        )
                    }
                }
            )

            else -> SearchResultState.Idle
        }

    private fun searchContentStateEventHandler(event: SearchResultContentEvents) {
        when (event) {
            is SearchResultContentEvents.OnCreateAlbumClick -> Unit

            is SearchResultContentEvents.OnLongImageTap ->
                gridState.onItemLongClick(event.image.mediaImageId)

            is SearchResultContentEvents.OnImageClick -> {
                val isChecked = gridState.onItemClick(event.image.mediaImageId)
                if (isChecked) {
                    return
                }
                val images = state.value.getImages().map { it.uri.toUri() }
                val initialIndex = images.indexOf(event.image.uri.toUri())
                navigators.navController.navigate(
                    ExtractorImageViewerNavTarget(
                        images = images,
                        initialIndex = initialIndex
                    )
                )
            }
        }
    }
}