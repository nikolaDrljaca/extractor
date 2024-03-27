package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageGridState
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndices
import com.drbrosdev.extractor.ui.components.extractorimagegrid.checkedIndicesAsFlow
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtractorAlbumViewerViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val albumId: Long
) : ViewModel() {
    private val _imageUris = MutableStateFlow(emptyList<Uri>())
    val imageUris = _imageUris.asStateFlow()

    private val dialogSelection =
        MutableStateFlow<ExtractorAlbumDialogSelection>(ExtractorAlbumDialogSelection.None)

    private val _events = Channel<ExtractorAlbumViewerEvents>()
    val events = _events.receiveAsFlow()

    private val albumFlow = albumRepository.findAlbumByIdAsFlow(albumId)
        .filterNotNull()
        .onEach { album -> _imageUris.update { getUris(album.entries) } }
        .flowOn(Dispatchers.Default)

    val gridState = ExtractorImageGridState()

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

    private suspend fun getUris(entries: List<AlbumEntry>) = withContext(Dispatchers.Default) {
        entries.map { it.uri.toUri() }
    }

    fun onDismissDialog() = dialogSelection.update { ExtractorAlbumDialogSelection.None }

    fun onDeleteAlbum() {
        viewModelScope.launch {
            dialogSelection.update { ExtractorAlbumDialogSelection.None }
            albumRepository.deleteAlbumById(albumId)
        }
    }

    fun onShareAction() {
        when {
            _imageUris.value.size > 30 -> dialogSelection.update { ExtractorAlbumDialogSelection.ConfirmShare }
            else -> {
                dialogSelection.update { ExtractorAlbumDialogSelection.None }
                viewModelScope.launch {
                    _events.send(ExtractorAlbumViewerEvents.SelectionShared)
                }
            }
        }
    }

    fun onSelectionClear() {
        gridState.clearSelection()
    }

    fun getSelectedUris(): List<Uri> {
        return gridState.checkedIndices().map { imageUris.value[it] }
    }

    fun onSelectionCreate() {
        viewModelScope.launch {
            val album = state.value.getAlbum()
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
            val album = state.value.getAlbum()
            val ids = gridState.checkedIndices().map { album.entries[it].id.id }

            val albumCount = album.entries.count()
            val selectedCount = gridState.checkedIndices().count()

            if (albumCount == selectedCount) {
                albumRepository.deleteAlbumById(albumId)
                _events.send(ExtractorAlbumViewerEvents.AlbumDeleted)
            }

            if (ids.isNotEmpty()) {
                albumRepository.deleteAlbumItems(ids)
                _events.send(ExtractorAlbumViewerEvents.SelectionDeleted)
            }
            gridState.clearSelection()
        }
    }
}