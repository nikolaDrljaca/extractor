package com.drbrosdev.extractor.ui.albumviewer

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.MediaImageUri

@Stable
data class ExtractorAlbumViewerState(
    val id: Long,
    val hero: AlbumHeroUiModel,
    val entries: List<AlbumEntry>,
    val shouldShowSelectBar: Boolean,
    val eventSink: (AlbumViewerEvents) -> Unit
)

@Stable
data class AlbumHeroUiModel(
    val name: String,
    val description: String,
    val heroImage: MediaImageUri
) {
    val displayName = "# $name"
}

@Immutable
sealed interface AlbumViewerEvents {
    data object Delete : AlbumViewerEvents

    data object Share : AlbumViewerEvents


    data object GoBack : AlbumViewerEvents

    data class GoToImageViewer(
        val index: Int
    ) : AlbumViewerEvents
}
