package com.drbrosdev.extractor.util

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentTransitionScope
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
