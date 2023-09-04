package com.drbrosdev.extractor.util

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.NavTransitionSpec


interface NavTarget : Parcelable {

    @Composable
    fun Content()
}

val LocalNavController = staticCompositionLocalOf<NavController<NavTarget>> {
    error("NavController was not instantiated, or is unavailable in this context.")
}


val SlideTransitionSpec = NavTransitionSpec<Any?> { action, _, _ ->
    val direction = if (action == NavAction.Pop) {
        AnimatedContentTransitionScope.SlideDirection.End
    } else {
        AnimatedContentTransitionScope.SlideDirection.Start
    }
    slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
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
