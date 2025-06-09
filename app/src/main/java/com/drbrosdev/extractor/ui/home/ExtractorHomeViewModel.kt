package com.drbrosdev.extractor.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.None
import arrow.core.Some
import arrow.core.toOption
import com.drbrosdev.extractor.domain.model.Album
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.asString
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.usecase.generate.GenerateUserCollage
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerNavTarget
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorAlbumOverview
import com.drbrosdev.extractor.ui.components.albumoverview.ExtractorAlbumsUiModel
import com.drbrosdev.extractor.util.WhileUiSubscribed
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
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
    private val navigators: Navigators
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
                it.isEmpty() -> ExtractorAlbumsUiModel.Empty
                else -> ExtractorAlbumsUiModel.Content(
                    albums = it.map { album -> toOverview(album) },
                    onAlbumClick = { id ->
                        navigators.navController.navigate(ExtractorAlbumViewerNavTarget(id))
                    }
                )
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorAlbumsUiModel.Empty
        )

    private fun toOverview(album: Album): ExtractorAlbumOverview {
        val images = album.entries
            .take(8)
            .map { it.uri }
        return ExtractorAlbumOverview(
            albumId = album.id,
            title = album.name,
            searchType = album.searchType.asString(),
            images = images,
            photoCount = album.entries.size
        )
    }
}