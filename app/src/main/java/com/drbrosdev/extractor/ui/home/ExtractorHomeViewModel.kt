package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorHomeScreenSettings
import com.drbrosdev.extractor.domain.usecase.settings.ProvideHomeScreenSettings
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.util.toPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtractorHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val compileVisualAlbum: CompileVisualAlbum,
    private val compileTextAlbum: CompileTextAlbums,
    private val albumRepository: AlbumRepository,
    private val homeScreenSettingsProvider: ProvideHomeScreenSettings
) : ViewModel() {

    val settings = homeScreenSettingsProvider()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorHomeScreenSettings()
        )

    val textAlbums = albumRepository
        .getCommonTextAlbumsAsFlow()
        .map { albums ->
            when {
                albums.isEmpty() -> ExtractorCategoryViewState.Initial
                else -> ExtractorCategoryViewState.Content(albums = albums.map { it.toPreview() })
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial
        )

    val visualAlbums = albumRepository
        .getCommonVisualAlbumsAsFlow()
        .map { albums ->
            when {
                albums.isEmpty() -> ExtractorCategoryViewState.Initial
                else -> ExtractorCategoryViewState.Content(albums = albums.map { it.toPreview() })
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial
        )

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
            compileVisualAlbum()
        }
    }

    fun compileTextAlbums() {
        viewModelScope.launch {
            compileTextAlbum()
        }
    }
}