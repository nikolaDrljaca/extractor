package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.domain.usecase.SpawnAlbumCleanupWork
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndicesAsFlow
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    private val spawnAlbumCleanupWork: SpawnAlbumCleanupWork,
    private val albumRepository: AlbumRepository,
    private val albumId: Long
) : ViewModel() {

    private val dialogSelection =
        MutableStateFlow<ExtractorAlbumDialogSelection>(ExtractorAlbumDialogSelection.None)

    private val _events = Channel<ExtractorAlbumViewerEvents>()
    val events = _events.receiveAsFlow()

    private val albumFlow = albumRepository.findAlbumByIdAsFlow(albumId)
        .onStart { spawnAlbumCleanupWork.invoke(albumId) }
        .filterNotNull()
        .flowOn(Dispatchers.Default)

    val gridState = ExtractorGridState()

    private val shouldShowSelectAction = gridState.checkedIndicesAsFlow()
        .map { it.isNotEmpty() }

    val state = combine(
        albumFlow,
        dialogSelection,
        shouldShowSelectAction
    ) { album, dialog, showSelectBar ->
        ExtractorAlbumViewerScreenState.Content(
            album = album,
            dialogSelection = dialog,
            shouldShowSelectBar = showSelectBar
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorAlbumViewerScreenState.Loading
        )

    fun onDismissDialog() = dialogSelection.update { ExtractorAlbumDialogSelection.None }

    fun onDeleteAlbum() {
        viewModelScope.launch {
            dialogSelection.update { ExtractorAlbumDialogSelection.None }
            albumRepository.deleteAlbumById(albumId)
        }
    }

    fun onShareAction() {
        val albumEntries = state.value.albumEntries()
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

    fun onShareConfirmed() {
        val albumEntries = state.value.albumEntries()
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
        val entries = state.value.albumEntries()
            .map { it.uri.toUri() }
        return gridState.checkedIndices().map { entries[it] }
    }

    fun onSelectionCreate() {
        viewModelScope.launch {
            val album = state.value.album()
            val newAlbum = NewAlbum(
                name = album.name,
                keyword = album.keyword,
                keywordType = album.keywordType,
                searchType = album.searchType,
                origin = NewAlbum.Origin.USER_GENERATED,
                entries = gridState.checkedIndices().map { album.entries[it] }.map {
                    NewAlbum.Entry(
                        uri = it.uri,
                        id = it.id
                    )
                }
            )

            albumRepository.createAlbum(newAlbum)
            gridState.clearSelection()
            _events.send(ExtractorAlbumViewerEvents.SelectionCreated)
        }
    }

    fun onBottomSheetDelete() {
        dialogSelection.update { ExtractorAlbumDialogSelection.ConfirmDelete }
    }

    fun onShowBottomSheet() {
        dialogSelection.update { ExtractorAlbumDialogSelection.BottomSheet }
    }

    fun onDeleteSelection() {
        viewModelScope.launch {
            val album = state.value.album()
            val ids = gridState.checkedIndices().map { album.entries[it].id.id }

            val albumCount = album.entries.count()
            val selectedCount = gridState.checkedIndices().count()

            if (albumCount == selectedCount) {
                albumRepository.deleteAlbumById(albumId)
                _events.send(ExtractorAlbumViewerEvents.AlbumDeleted)
            }

            if (ids.isNotEmpty()) {
                albumRepository.deleteAlbumEntries(ids)
                _events.send(ExtractorAlbumViewerEvents.SelectionDeleted)
            }
            gridState.clearSelection()
        }
    }

    fun onNavigateToViewer(index: Int) {
        viewModelScope.launch {
            val albums = state.value.albumEntries()
                .map { it.uri.toUri() }
            _events.send(
                ExtractorAlbumViewerEvents.NavigateToImageViewer(
                    uris = albums,
                    initialIndex = index
                )
            )
        }
    }
}