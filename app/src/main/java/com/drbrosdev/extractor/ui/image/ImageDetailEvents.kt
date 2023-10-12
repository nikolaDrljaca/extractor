package com.drbrosdev.extractor.ui.image

import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem

sealed interface ImageDetailEvents {
    data object OnShare : ImageDetailEvents

    data object OnEdit : ImageDetailEvents

    data object OnUseAs : ImageDetailEvents

    data object OnExtractorInfo : ImageDetailEvents
}

fun ExtractorBottomBarItem.toEvent(): ImageDetailEvents {
    return when (this) {
        ExtractorBottomBarItem.SHARE -> ImageDetailEvents.OnShare
        ExtractorBottomBarItem.EDIT -> ImageDetailEvents.OnEdit
        ExtractorBottomBarItem.USE_AS -> ImageDetailEvents.OnUseAs
        ExtractorBottomBarItem.EX_INFO -> ImageDetailEvents.OnExtractorInfo
    }
}
