package com.drbrosdev.extractor.util

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.drbrosdev.extractor.ui.album.ExtractorAlbumNavTarget
import com.drbrosdev.extractor.ui.image.ExtractorImageNavTarget
import com.drbrosdev.extractor.ui.search.ExtractorSearchNavTarget
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavTransitionSpec
import dev.olshevski.navigation.reimagined.material.BottomSheetState


interface NavTarget : Parcelable {

    @Composable
    fun Content()
}

interface DialogNavTarget : Parcelable {

    @Composable
    fun Content()
}

interface BottomSheetNavTarget : Parcelable {

    @Composable
    fun Content(sheetState: BottomSheetState)
}

val LocalNavController = staticCompositionLocalOf<NavController<NavTarget>> {
    error("NavController was not instantiated, or is unavailable in this context.")
}

val LocalDialogNavController = staticCompositionLocalOf<NavController<DialogNavTarget>> {
    error("DialogNavController was not instantiated, or is unavailable in this context.")
}

val LocalBottomSheetNavController = staticCompositionLocalOf<NavController<BottomSheetNavTarget>> {
    error("BottomSheetNavController was not instantiated, or is unavailable in this scope.")
}


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

    when {
        ((from is ExtractorSearchNavTarget) or (from is ExtractorAlbumNavTarget)) and (to is ExtractorImageNavTarget) -> fadeInAndOut
        (from is ExtractorImageNavTarget) and ((to is ExtractorAlbumNavTarget) or (to is ExtractorSearchNavTarget)) -> fadeInAndOut
        else -> default
    }
}


val CardStackSpec = NavTransitionSpec<Any?> { action, _, _ ->
    val slide = when (action) {
        is NavAction.Navigate -> {
            fadeIn() + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween()
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween()
            )
        }

        is NavAction.Pop -> {
            slideInVertically(initialOffsetY = { it * 2 }) togetherWith scaleOut(targetScale = 0.9f)
        }

        else -> error("")
    }

    slide
}