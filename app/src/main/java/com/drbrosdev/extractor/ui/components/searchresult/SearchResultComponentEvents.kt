package com.drbrosdev.extractor.ui.components.searchresult

import android.net.Uri

sealed interface SearchResultComponentEvents {
    data object SearchComplete : SearchResultComponentEvents

    data object ScrollToTop : SearchResultComponentEvents

    data class Share(val images: List<Uri>) : SearchResultComponentEvents
}