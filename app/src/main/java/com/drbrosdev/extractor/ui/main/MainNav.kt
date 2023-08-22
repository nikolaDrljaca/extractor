package com.drbrosdev.extractor.ui.main

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface MainRoutes : Parcelable {

    @Parcelize
    data object SearchRoute : MainRoutes

    @Parcelize
    data class ImageDetailRoute(
        val images: List<Uri>,
        val initialIndex: Int
    ) : MainRoutes

    @Parcelize
    data object AboutRoute : MainRoutes
}

interface MainNavigator {

    fun toImageDetailRoute(args: NavToImageNodeArgs)
}

data class NavToImageNodeArgs(
    val images: List<Uri>,
    val initialIndex: Int
)
