package com.drbrosdev.extractor.ui.imageviewer

import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem

sealed interface ExtractorImageViewerEvents {
    data object OnShare : ExtractorImageViewerEvents

    data object OnEdit : ExtractorImageViewerEvents

    data object OnUseAs : ExtractorImageViewerEvents

    data object OnExtractorInfo : ExtractorImageViewerEvents
}

fun ExtractorBottomBarItem.toEvent(): ExtractorImageViewerEvents {
    return when (this) {
        ExtractorBottomBarItem.SHARE -> ExtractorImageViewerEvents.OnShare
        ExtractorBottomBarItem.EDIT -> ExtractorImageViewerEvents.OnEdit
        ExtractorBottomBarItem.USE_AS -> ExtractorImageViewerEvents.OnUseAs
        ExtractorBottomBarItem.EX_INFO -> ExtractorImageViewerEvents.OnExtractorInfo
    }
}
