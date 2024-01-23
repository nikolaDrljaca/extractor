package com.drbrosdev.extractor.ui.allalbum

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorAlbumsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val confirmDeleteDialog = MutableStateFlow(false)

    private val selectedAlbumId = savedStateHandle.getStateFlow<Long?>(SELECTED_ALBUM_ID, null)

    val state = albumRepository.getAllUserAlbumsAsFlow()
        .combine(confirmDeleteDialog) { albums, showDelete ->
            ExtractorAlbumsScreenState.Content(
                albums = albums.map { entry -> entry.toItemUiModel() },
                isConfirmDeleteShown = showDelete
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorAlbumsScreenState.Loading
        )

    fun onDeleteAction(albumId: Long) {
        savedStateHandle[SELECTED_ALBUM_ID] = albumId
        confirmDeleteDialog.update { true }
    }

    fun onDeleteAlbum() {
        viewModelScope.launch {
            selectedAlbumId.value?.let { albumRepository.deleteAlbumById(it) }
            confirmDeleteDialog.update { false }
        }
    }

    fun onDismissDialog() {
        confirmDeleteDialog.update { false }
    }

    companion object {
        private const val SELECTED_ALBUM_ID = "selected_album_id"
    }
}