package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri


sealed interface ExtractorAlbumViewerEvents {

    data object SelectionCreated : ExtractorAlbumViewerEvents

    data object SelectionDeleted : ExtractorAlbumViewerEvents

    data object AlbumDeleted : ExtractorAlbumViewerEvents

    data class NavigateToImageViewer(
        val uris: List<Uri>,
        val initialIndex: Int
    ) : ExtractorAlbumViewerEvents

    data class ShareAlbumEntries(
        val uris: List<Uri>
    ) : ExtractorAlbumViewerEvents
}
