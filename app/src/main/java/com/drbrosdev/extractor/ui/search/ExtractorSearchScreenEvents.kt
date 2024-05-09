package com.drbrosdev.extractor.ui.search

import android.net.Uri

sealed interface ExtractorSearchScreenEvents {
    data class NavToImage(
        val imageUris: List<Uri>,
        val initialIndex: Int
    ) : ExtractorSearchScreenEvents

    data object AlbumCreated : ExtractorSearchScreenEvents

    data object NavToGetMore : ExtractorSearchScreenEvents

    data object ShowSearchSheet : ExtractorSearchScreenEvents

    data object HideKeyboard: ExtractorSearchScreenEvents

    data class ShareSelectedImages(
        val imageUris: List<Uri>
    ): ExtractorSearchScreenEvents
}