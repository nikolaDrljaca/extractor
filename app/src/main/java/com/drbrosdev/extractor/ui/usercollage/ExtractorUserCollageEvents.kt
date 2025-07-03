package com.drbrosdev.extractor.ui.usercollage

import android.net.Uri

sealed interface ExtractorUserCollageEvents {
    data class NavToImageViewer(
        val initialIndex: Int,
        val images: List<Uri>
    ) : ExtractorUserCollageEvents

    data class ShareCollage(
        val images: List<Uri>
    ) : ExtractorUserCollageEvents
}