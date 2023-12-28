package com.drbrosdev.extractor.ui.album

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.util.toUri
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExtractorAlbumViewModel(
    private val stateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val albumId: Long
) : ViewModel() {

    val state = albumRepository.findAlbumByIdAsFlow(albumId)
        .map { ExtractorAlbumScreenState.Content(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ExtractorAlbumScreenState.Loading)

    fun getImageUris(): List<Uri> {
        return when (val out = state.value) {
            is ExtractorAlbumScreenState.Content -> out.album.entries.map { it.uri.toUri() }
            ExtractorAlbumScreenState.Loading -> emptyList()
        }
    }
}