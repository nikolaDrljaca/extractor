package com.drbrosdev.extractor.framework.navigation.animspec

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.Density
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerNavTarget
import com.drbrosdev.extractor.ui.getmore.ExtractorGetMoreNavTarget
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionSpec

fun createTransitionSpec(density: Density) = NavTransitionSpec<NavTarget?> { action, from, to ->
    val fade = FadeEnterTransition togetherWith FadeExitTransition
    val fadeThrough = createFadeThrough(action)
    val default = createDefaultTransition(action, density)

    when {
        goingToImageViewer(from, to) -> fade
        goingFromImageViewer(from, to) -> fade

        // handle transitions for GetMore Screen
        handleGetMore(from, to) -> fadeThrough

        else -> default
    }
}

private fun createFadeThrough(
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
        is NavAction.Pop, NavAction.Replace -> SharedXAxisPopEnterTransition(density) togetherWith SharedXAxisPopExitTransition(
            density
        )

        else -> SharedXAxisEnterTransition(density) togetherWith SharedXAxisExitTransition(density)
    }
}


private fun goingFromImageViewer(from: NavTarget?, to: NavTarget?): Boolean {
    val goingFrom = from is ExtractorImageViewerNavTarget
    val goingTo = (to is ExtractorAlbumViewerNavTarget) or (to is ExtractorSearchNavTarget)

    return goingFrom and goingTo
}

private fun handleGetMore(
    from: NavTarget?,
    to: NavTarget?
): Boolean {
    val goingFrom = when (from) {
        is ExtractorSearchNavTarget -> true
        is ExtractorGetMoreNavTarget -> true
        else -> false
    }
    val goingTo = when (to) {
        is ExtractorSearchNavTarget -> true
        is ExtractorGetMoreNavTarget -> true
        else -> false
    }
    return goingFrom and goingTo
}

private fun goingToImageViewer(
    from: NavTarget?,
    to: NavTarget?
): Boolean {
    val goingFrom = (from is ExtractorSearchNavTarget) or (from is ExtractorAlbumViewerNavTarget)
    val goingTo = to is ExtractorImageViewerNavTarget

    return goingFrom and goingTo
}
