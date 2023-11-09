package com.drbrosdev.extractor.ui.image

import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem

sealed interface ExtractorImageEvents {
    data object OnShare : ExtractorImageEvents

    data object OnEdit : ExtractorImageEvents

    data object OnUseAs : ExtractorImageEvents

    data object OnExtractorInfo : ExtractorImageEvents
}

fun ExtractorBottomBarItem.toEvent(): ExtractorImageEvents {
    return when (this) {
        ExtractorBottomBarItem.SHARE -> ExtractorImageEvents.OnShare
        ExtractorBottomBarItem.EDIT -> ExtractorImageEvents.OnEdit
        ExtractorBottomBarItem.USE_AS -> ExtractorImageEvents.OnUseAs
        ExtractorBottomBarItem.EX_INFO -> ExtractorImageEvents.OnExtractorInfo
    }
}
