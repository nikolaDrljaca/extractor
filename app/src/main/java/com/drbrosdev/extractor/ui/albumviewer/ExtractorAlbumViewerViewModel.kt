package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeys
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedKeysAsFlow
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.util.WhileUiSubscribed
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorAlbumViewerViewModel(
    private val stateHandle: SavedStateHandle,
    private val workerService: ExtractorWorkerService,
    private val albumRepository: AlbumRepository,
    private val navigators: Navigators,
    private val albumId: Long
) : ViewModel() {
    private val navController = navigators.navController

    private val dialogSelection =
        MutableStateFlow<ExtractorAlbumDialogSelection>(ExtractorAlbumDialogSelection.None)
    val dialogSelectionState = dialogSelection.asStateFlow()

    private val _events = Channel<ExtractorAlbumViewerEvents>()
    val events = _events.receiveAsFlow()

    private val albumFlow = albumRepository.findAlbumByIdAsFlow(albumId)
        .onStart { workerService.startAlbumCleanupWorker(albumId) }
        .filterNotNull()
        .flowOn(Dispatchers.Default)

    val gridState = ExtractorGridState<Int>()

    private val shouldShowSelectAction = gridState.checkedKeysAsFlow()
        .map { it.isNotEmpty() }

    val state = combine(
        albumFlow,
        shouldShowSelectAction
    ) { album, showSelectBar ->
        ExtractorAlbumViewerState(
            id = album.id,
            hero = AlbumHeroUiModel(
                name = album.name,
                description =
                    "${album.keywordType.name.lowercase()} \u00B7 ${album.searchType.name.lowercase()} \u00B7 ${album.entries.size}",
                heroImage = album.entries.first().uri
            ),
            entries = album.entries,
            shouldShowSelectBar = showSelectBar,
            eventSink = ::eventHandler
        )
    }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            null
        )

    private fun eventHandler(event: AlbumViewerEvents) {
        when (event) {
            AlbumViewerEvents.Delete -> {
                dialogSelection.update { ExtractorAlbumDialogSelection.ConfirmDelete }
            }

            AlbumViewerEvents.GoBack -> navController.pop()

            is AlbumViewerEvents.GoToImageViewer -> {
                val current = state.value ?: return
                val albumEntries = current.entries
                    .map { it.uri.toUri() }
                val destination = ExtractorImageViewerNavTarget(
                    images = albumEntries,
                    initialIndex = event.index
                )
                navController.navigate(destination)
            }

            AlbumViewerEvents.Share -> {
                val current = state.value ?: return
                val albumEntries = current.entries
                    .map { it.uri.toUri() }

                when {
                    albumEntries.size > 30 -> dialogSelection.update { ExtractorAlbumDialogSelection.ConfirmShare }
                    else -> {
                        dialogSelection.update { ExtractorAlbumDialogSelection.None }
                        viewModelScope.launch {
                            _events.send(ExtractorAlbumViewerEvents.ShareAlbumEntries(albumEntries))
                        }
                    }
                }
            }
        }
    }

    fun onDismissDialog() = dialogSelection.update { ExtractorAlbumDialogSelection.None }

    fun onDeleteAlbum() {
        viewModelScope.launch {
            dialogSelection.update { ExtractorAlbumDialogSelection.None }
            albumRepository.deleteAlbumById(albumId)
            navController.pop()
        }
    }

    fun onShareConfirmed() {
        val current = state.value ?: return
        val albumEntries = current.entries
            .map { it.uri.toUri() }
        viewModelScope.launch {
            _events.send(ExtractorAlbumViewerEvents.ShareAlbumEntries(albumEntries))
        }
    }

    fun onSelectionClear() {
        gridState.clearSelection()
    }

    // NOTE: Potential bottleneck with albums of very large size
    fun getSelectedUris(): List<Uri> {
        val current = state.value ?: return emptyList()
        val entries = current.entries
            .map { it.uri.toUri() }
        return gridState.checkedKeys().map { entries[it] }
    }

    fun onSelectionCreate() {
    }

    fun onDeleteSelection() {
        val current = state.value ?: return
        viewModelScope.launch {
            val ids = gridState.checkedKeys().map { current.entries[it].id.id }

            val albumCount = current.entries.count()
            val selectedCount = gridState.checkedKeys().count()

            // all images were selected - delete album and pop out
            if (albumCount == selectedCount) {
                albumRepository.deleteAlbumById(albumId)
                navController.pop()
                return@launch
            }

            if (ids.isNotEmpty()) {
                albumRepository.deleteAlbumEntries(ids)
            }
            gridState.clearSelection()
        }
    }
}