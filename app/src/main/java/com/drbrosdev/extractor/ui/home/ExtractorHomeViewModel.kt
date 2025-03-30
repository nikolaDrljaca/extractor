package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toPreview
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.generate.GenerateUserCollage
import com.drbrosdev.extractor.ui.components.categoryview.ExtractorCategoryViewState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface ExtractorUserCollageThumbnailUiState {
    data object Empty : ExtractorUserCollageThumbnailUiState

    data class Content(
        val mediaImageUri: MediaImageUri,
        val keywords: String
    ) : ExtractorUserCollageThumbnailUiState
}

class ExtractorHomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val generateUserCollage: GenerateUserCollage,
) : ViewModel() {

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
}