package com.drbrosdev.extractor.ui.albumviewer


sealed interface ExtractorAlbumViewerEvents {

    data object SelectionShared: ExtractorAlbumViewerEvents

    data object SelectionCreated : ExtractorAlbumViewerEvents

    data object SelectionDeleted : ExtractorAlbumViewerEvents

    data object AlbumDeleted : ExtractorAlbumViewerEvents
}
