package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.CompileVisualAlbum
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.util.toPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val compileVisualAlbum: CompileVisualAlbum,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _visualAlbums =
        MutableStateFlow<ExtractorCategoryViewState>(ExtractorCategoryViewState.Initial)

    val visualAlbums = _visualAlbums.asStateFlow()

    private val _textAlbums =
        MutableStateFlow<ExtractorCategoryViewState>(ExtractorCategoryViewState.Initial)
    val textAlbums = _textAlbums.asStateFlow()

    private val getVisualAlbumsJob = albumRepository
        .getCommonVisualAlbumsAsFlow()
        .onStart { _visualAlbums.update { ExtractorCategoryViewState.Loading } }
        .onEach { albums ->
            val out = albums.map { it.toPreview() }
            _visualAlbums.update {
                when {
                    albums.isEmpty() -> ExtractorCategoryViewState.Initial
                    else -> ExtractorCategoryViewState.Content(albums = albums.map { it.toPreview() })
                }
            }
        }
        .take(1)
        .flowOn(Dispatchers.Default)
        .launchIn(viewModelScope)

    val userAlbums = albumRepository
        .getAllUserAlbumsAsFlow()
        .map {
            when {
                it.isEmpty() -> ExtractorCategoryViewState.Initial
                else -> ExtractorCategoryViewState.Content(albums = it.map { album -> album.toPreview() })
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial
        )

    fun compileVisualAlbums() {
        viewModelScope.launch {
            _visualAlbums.update { ExtractorCategoryViewState.Loading }

            compileVisualAlbum()

            val out = albumRepository.getCommonVisualAlbums()
                .map { it.toPreview() }

            when {
                out.isEmpty() -> {
                    _visualAlbums.update { ExtractorCategoryViewState.Empty }
                }

                else -> {
                    _visualAlbums.update {
                        ExtractorCategoryViewState.Content(
                            albums = out
                        )
                    }
                }
            }
        }
    }
}