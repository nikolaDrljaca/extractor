package com.drbrosdev.extractor.ui.album

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
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

    val state = albumRepository.findAlbumByIdAsFlow(albumId)
        .filterNotNull()
        .onEach { album -> _imageUris.update { getUris(album.entries) } }
        .combine(confirmDeleteDialog) { album, showConfirm ->
            ExtractorAlbumScreenState.Content(
                album = album,
                isConfirmDeleteShown = showConfirm
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
}