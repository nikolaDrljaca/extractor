package com.drbrosdev.extractor.framework.navigation.animspec

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.Density
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerNavTarget
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.overview.ExtractorOverviewNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import com.drbrosdev.extractor.ui.shop.ExtractorShopNavTarget
import com.drbrosdev.extractor.ui.usercollage.ExtractorUserCollageNavTarget
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionSpec

fun createTransitionSpec(density: Density) = NavTransitionSpec<NavTarget?> { action, from, to ->
    val fade = FadeEnterTransition togetherWith FadeExitTransition
    val fadeThrough = createZAxisTransition(action)
    val default = createDefaultTransition(action, density)

    when {
        // handle image viewer
        handleImageViewer(from, to) -> fade

        // handle search transitions
        handleSearch(from, to) -> fadeThrough

        // handle transitions for GetMore Screen
        handleGetMore(from, to) -> fadeThrough

        else -> default
    }
}

private fun createZAxisTransition(
    action: NavAction,
): ContentTransform {
    return when (action) {
        is NavAction.Pop, NavAction.Replace -> SharedZAxisPopEnterTransition togetherWith SharedZAxisPopExitTransition
        else -> SharedZAxisEnterTransition togetherWith SharedZAxisExitTransition
    }
}

private fun createDefaultTransition(
    action: NavAction,
    density: Density
): ContentTransform {
    return when (action) {
        is NavAction.Pop, NavAction.Replace ->
            SharedXAxisPopEnterTransition(density) togetherWith SharedXAxisPopExitTransition(density)

        else -> SharedXAxisEnterTransition(density) togetherWith SharedXAxisExitTransition(density)
    }
}

private fun handleGetMore(
    from: NavTarget?,
    to: NavTarget?
): Boolean {
    val goingFrom = when (from) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorShopNavTarget -> true
        else -> false
    }
    val goingTo = when (to) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorShopNavTarget -> true
        else -> false
    }
    return goingFrom and goingTo
}

private fun handleSearch(from: NavTarget?, to: NavTarget?): Boolean {
    val goingFrom = when (from) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorSearchNavTarget -> true
        else -> false
    }
    val goingTo = when (to) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorSearchNavTarget -> true
        else -> false
    }
    return goingFrom and goingTo
}

private fun handleImageViewer(from: NavTarget?, to: NavTarget?): Boolean {
    val goingFrom = when (from) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorAlbumViewerNavTarget -> true
        is ExtractorUserCollageNavTarget -> true
        is ExtractorImageViewerNavTarget -> true
        else -> false
    }
    val goingTo = when (to) {
        is ExtractorOverviewNavTarget -> true
        is ExtractorAlbumViewerNavTarget -> true
        is ExtractorUserCollageNavTarget -> true
        is ExtractorImageViewerNavTarget -> true
        else -> false
    }
    return goingFrom and goingTo
}
