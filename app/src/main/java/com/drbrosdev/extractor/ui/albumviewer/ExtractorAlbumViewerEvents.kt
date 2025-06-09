package com.drbrosdev.extractor.ui.albumviewer

import android.net.Uri


sealed interface ExtractorAlbumViewerEvents {

    data class ShareAlbumEntries(
        val uris: List<Uri>
    ) : ExtractorAlbumViewerEvents
}
