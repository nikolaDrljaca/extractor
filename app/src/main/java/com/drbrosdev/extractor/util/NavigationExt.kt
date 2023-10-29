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
import com.drbrosdev.extractor.ui.image.ImageDetailNavTarget
import com.drbrosdev.extractor.ui.result.SearchResultNavTarget
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

interface BottomSheetNavTarget: Parcelable {

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


val SlideTransitionSpec = NavTransitionSpec<NavTarget?> { action, from, to ->
    if ((from is SearchResultNavTarget) and (to is  ImageDetailNavTarget)) {
        return@NavTransitionSpec fadeIn() togetherWith fadeOut()
    }

    if ((to is SearchResultNavTarget) and (from is  ImageDetailNavTarget)) {
        return@NavTransitionSpec fadeIn() togetherWith fadeOut()
    }

    val direction = if (action == NavAction.Pop) {
        AnimatedContentTransitionScope.SlideDirection.End
    } else {
        AnimatedContentTransitionScope.SlideDirection.Start
    }

    fadeIn() + slideIntoContainer(direction) togetherWith fadeOut() + slideOutOfContainer(direction)
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
