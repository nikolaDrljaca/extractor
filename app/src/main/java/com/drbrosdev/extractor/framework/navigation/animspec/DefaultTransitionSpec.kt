package com.drbrosdev.extractor.framework.navigation.animspec

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerNavTarget
import com.drbrosdev.extractor.ui.getmore.ExtractorGetMoreNavTarget
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionSpec


val DefaultTransitionSpec = NavTransitionSpec<NavTarget?> { action, from, to ->
    val direction = when (action) {
        is NavAction.Pop, NavAction.Replace -> AnimatedContentTransitionScope.SlideDirection.End
        else -> AnimatedContentTransitionScope.SlideDirection.Start
    }

    val default =
        fadeIn() + slideIntoContainer(direction) togetherWith fadeOut() + slideOutOfContainer(
            direction
        )

    val fadeInAndOut = fadeIn() togetherWith fadeOut()

    val scaleInAndOut =
        fadeIn() + scaleIn(initialScale = 0.9f) togetherWith fadeOut()

    when {
        ((from is ExtractorSearchNavTarget) or (from is ExtractorAlbumViewerNavTarget)) and (to is ExtractorImageViewerNavTarget) -> fadeInAndOut
        (from is ExtractorImageViewerNavTarget) and ((to is ExtractorAlbumViewerNavTarget) or (to is ExtractorSearchNavTarget)) -> fadeInAndOut

        // handle transitions for GetMore Screen
        (from is ExtractorSearchNavTarget) and (to is ExtractorGetMoreNavTarget) -> scaleInAndOut
        (to is ExtractorSearchNavTarget) and (from is ExtractorGetMoreNavTarget) -> scaleInAndOut

        else -> default
    }
}
