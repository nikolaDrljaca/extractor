package com.drbrosdev.extractor.ui.main

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface MainRoutes : Parcelable {

    @Parcelize
    data object HomeRoute : MainRoutes

    @Parcelize
    data object SyncStatusRoute : MainRoutes

    @Parcelize
    data class SearchResultRoute(
        val query: String
    ) : MainRoutes

    @Parcelize
    data object AboutRoute : MainRoutes
}

interface MainNavigator {

    fun toSearchResultRoute(args: SearchResultRouteArgs)
}

data class SearchResultRouteArgs(val query: String)

data class NavToImageNodeArgs(
    val images: List<Uri>,
    val initialIndex: Int
)
