package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.ExtractionStatus
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toPreview
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.album.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.settings.ExtractorHomeScreenSettings
import com.drbrosdev.extractor.domain.usecase.settings.ProvideHomeScreenSettings
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ExtractorUserCollageThumbnailUiState {
    data object Empty : ExtractorUserCollageThumbnailUiState

    data class Content(
        val mediaImageUri: MediaImageUri,
        val keywords: String
    ) : ExtractorUserCollageThumbnailUiState
}

class ExtractorHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val compileVisualAlbum: CompileVisualAlbum,
    private val compileTextAlbum: CompileTextAlbums,
    private val albumRepository: AlbumRepository,
    private val generateUserCollage: GenerateUserCollage,
    private val homeScreenSettingsProvider: ProvideHomeScreenSettings,
    private val extractionStatus: TrackExtractionProgress
) : ViewModel() {
    private val loadingTextAlbum = MutableStateFlow(false)
    private val loadingVisualAlbum = MutableStateFlow(false)

    val settings = homeScreenSettingsProvider.invoke()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ExtractorHomeScreenSettings()
        )

    val collage = flow {
        emit(generateUserCollage.invoke().firstOrNull())
    }
        .map { it.toOption() }
        .map {
            val out = it.flatMap { collage -> collage.extractions.firstOrNull().toOption() }
            val thumbnail = when (out) {
                None -> ExtractorUserCollageThumbnailUiState.Empty
                is Some -> ExtractorUserCollageThumbnailUiState.Content(
                    mediaImageUri = out.value.uri,
                    keywords = it.getOrNull()?.userEmbed ?: ""
                )
            }
            thumbnail
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorUserCollageThumbnailUiState.Empty
        )

    private val progress = extractionStatus.invoke()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val textAlbums = albumRepository
        .getCommonTextAlbumsAsFlow()
        .combine(loadingTextAlbum) { albums, loading ->
            when {
                loading and albums.isEmpty() -> ExtractorCategoryViewState.Initial(true)
                albums.isEmpty() -> ExtractorCategoryViewState.Initial()
                else -> ExtractorCategoryViewState.Content(
                    albums = albums.map { it.toPreview() },
                    isLoading = loading
                )
            }
        }
        .combine(progress) { viewState, status ->
            when (status) {
                is ExtractionStatus.Done -> viewState
                is ExtractionStatus.Running -> ExtractorCategoryViewState.StillIndexing()
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
                loading and albums.isEmpty() -> ExtractorCategoryViewState.Initial(true)
                albums.isEmpty() -> ExtractorCategoryViewState.Initial()
                else -> ExtractorCategoryViewState.Content(
                    albums = albums.map { it.toPreview() },
                    isLoading = loading
                )
            }
        }
        .combine(progress) { viewState, status ->
            when {
                viewState is ExtractorCategoryViewState.Content -> viewState
                status is ExtractionStatus.Done -> viewState
                status is ExtractionStatus.Running -> ExtractorCategoryViewState.StillIndexing()
                else -> viewState
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