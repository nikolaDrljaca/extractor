package com.drbrosdev.extractor.framework.navigation.animspec

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavTransitionSpec


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
