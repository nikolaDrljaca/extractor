package com.drbrosdev.extractor.ui.components.recommendsearch

import android.net.Uri

sealed interface RecommendedSearchesEvents {
    data class ShareImages(val uris: List<Uri>) : RecommendedSearchesEvents

    data object AlbumCreated : RecommendedSearchesEvents
}