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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtractorHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val compileVisualAlbum: CompileVisualAlbum,
    private val compileTextAlbum: CompileTextAlbums,
    private val albumRepository: AlbumRepository,
    private val homeScreenSettingsProvider: ProvideHomeScreenSettings
) : ViewModel() {
    private val loadingTextAlbum = MutableStateFlow(false)
    private val loadingVisualAlbum = MutableStateFlow(false)

    private val runningJobs = mutableMapOf<String, Job>()

    val settings = homeScreenSettingsProvider()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorHomeScreenSettings()
        )

    val textAlbums = albumRepository
        .getCommonTextAlbumsAsFlow()
        .combine(loadingTextAlbum) { albums, loading ->
            when {
                loading && albums.isEmpty() -> ExtractorCategoryViewState.Initial(true)
                albums.isEmpty() -> ExtractorCategoryViewState.Initial()
                else -> ExtractorCategoryViewState.Content(
                    albums = albums.map { it.toPreview() },
                    isLoading = loading
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial(true)
        )

    val visualAlbums = albumRepository
        .getCommonVisualAlbumsAsFlow()
        .combine(loadingVisualAlbum) { albums, loading ->
            when {
                loading && albums.isEmpty() -> ExtractorCategoryViewState.Initial(true)
                albums.isEmpty() -> ExtractorCategoryViewState.Initial()
                else -> ExtractorCategoryViewState.Content(
                    albums = albums.map { it.toPreview() },
                    isLoading = loading
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial(true)
        )

    val userAlbums = albumRepository
        .getAllUserAlbumsAsFlow()
        .map {
            when {
                it.isEmpty() -> ExtractorCategoryViewState.Initial()
                else -> ExtractorCategoryViewState.Content(
                    albums = it.map { album -> album.toPreview() },
                    isLoading = false
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorCategoryViewState.Initial()
        )

    fun compileVisualAlbums() {
        if (loadingVisualAlbum.value) return

        viewModelScope.launch {
            loadingVisualAlbum.update { true }
            compileVisualAlbum()
            loadingVisualAlbum.update { false }
        }
    }

    fun compileTextAlbums() {
        if (loadingTextAlbum.value) return

        viewModelScope.launch {
            loadingTextAlbum.update { true }
            compileTextAlbum()
            loadingTextAlbum.update { false }
        }
    }
}