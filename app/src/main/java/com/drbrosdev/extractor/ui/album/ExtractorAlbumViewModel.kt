package com.drbrosdev.extractor.ui.album

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExtractorAlbumViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val albumId: Long
) : ViewModel() {
    private val _imageUris = MutableStateFlow(emptyList<Uri>())
    val imageUris = _imageUris.asStateFlow()

    private val confirmDeleteDialog = MutableStateFlow(false)
    private val confirmShareDialog = MutableStateFlow(false)

    private val albumFlow = albumRepository.findAlbumByIdAsFlow(albumId)
        .filterNotNull()
        .onEach { album -> _imageUris.update { getUris(album.entries) } }
        .flowOn(Dispatchers.Default)

    val gridState = ExtractorImageGridState()

    private val shouldShowSelectAction = gridState.checkedIndicesAsFlow()
        .map { it.isNotEmpty() }

    val state = combine(
        albumFlow,
        confirmDeleteDialog,
        confirmShareDialog,
        shouldShowSelectAction
    ) { album, showDelete, showShare, showSelectBar ->
        ExtractorAlbumScreenState.Content(
            album = album,
            isConfirmDeleteShown = showDelete,
            isConfirmShareShown = showShare,
            shouldShowSelectBar = showSelectBar
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorAlbumScreenState.Loading
        )

    private suspend fun getUris(entries: List<AlbumEntry>) = withContext(Dispatchers.Default) {
        entries.map { it.uri.toUri() }
    }

    fun onDeleteAction() = confirmDeleteDialog.update { true }

    fun onDismissDialog() = confirmDeleteDialog.update { false }

    fun onDeleteAlbum() {
        viewModelScope.launch {
            confirmDeleteDialog.update { false }
            albumRepository.deleteAlbumById(albumId)
        }
    }

    fun onDismissShareDialog() {
        confirmShareDialog.update { false }
    }

    fun onShareAction(action: () -> Unit) {
        when {
            _imageUris.value.size > 30 -> confirmShareDialog.update { true }
            else -> {
                confirmShareDialog.update { false }
                action()
            }
        }
    }

    fun onSelectionClear() {
        gridState.clearSelection()
    }

    fun getSelectedUris(): List<Uri> {
        return gridState.checkedIndices().map { imageUris.value[it] }
    }

    fun onSelectionCreate(onComplete: () -> Unit = {}) {
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
        }.invokeOnCompletion { onComplete() }
    }
}