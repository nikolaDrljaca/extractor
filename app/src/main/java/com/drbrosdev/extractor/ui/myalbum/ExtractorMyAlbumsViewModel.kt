package com.drbrosdev.extractor.ui.myalbum

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.payload.NewAlbum
import com.drbrosdev.extractor.framework.StringResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorMyAlbumsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {
    private val snackBarHostState = SnackbarHostState()

    val state = albumRepository.getAllUserAlbumsAsFlow()
        .map {
            ExtractorMyAlbumsScreenState.Content(
                albums = it.map { entry -> entry.toItemUiModel() },
                snackBarHostState = snackBarHostState
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorMyAlbumsScreenState.Loading(snackBarHostState)
        )

    fun onDeleteAlbum(value: AlbumItemUiModel) {
        viewModelScope.launch {
            val albumToBeDeleted = albumRepository.findAlbumById(value.id) ?: return@launch

            albumRepository.deleteAlbumById(albumToBeDeleted.id)

            val result = snackBarHostState.showSnackbar(
                message = stringProvider.get(R.string.snackbar_delete),
                actionLabel = stringProvider.get(R.string.snackbar_undo),
                duration = SnackbarDuration.Long
            )

            when (result) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> with(albumToBeDeleted) {
                    val newAlbum = NewAlbum(
                        keyword = keyword,
                        name = name,
                        searchType = searchType,
                        keywordType = keywordType,
                        origin = NewAlbum.Origin.USER_GENERATED,
                        entries = entries.map { NewAlbum.Entry(it.uri, it.id) }
                    )
                    albumRepository.createAlbum(newAlbum)
                }
            }
        }
    }

    fun onShareAlbum(value: AlbumItemUiModel) {
        val size = value.thumbnails.size

        if (size < 30) return

        viewModelScope.launch {
            snackBarHostState.showSnackbar(
                message = stringProvider.get(
                    R.string.snackbar_share,
                    size
                )
            )
        }
    }
}
