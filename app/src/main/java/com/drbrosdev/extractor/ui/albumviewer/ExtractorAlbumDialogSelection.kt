package com.drbrosdev.extractor.ui.albumviewer


sealed interface ExtractorAlbumDialogSelection {

    data object None : ExtractorAlbumDialogSelection

    data object ConfirmShare : ExtractorAlbumDialogSelection

    data object ConfirmDelete : ExtractorAlbumDialogSelection
}