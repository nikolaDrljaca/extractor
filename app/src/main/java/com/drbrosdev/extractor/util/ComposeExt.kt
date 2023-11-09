package com.drbrosdev.extractor.util

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.drbrosdev.extractor.ui.theme.md_theme_light_secondary
import com.drbrosdev.extractor.ui.theme.md_theme_light_tertiary

@Composable
fun applicationIconBitmap(): ImageBitmap {
    return LocalContext.current.packageManager
        .getApplicationIcon("com.drbrosdev.extractor")
        .toBitmap(config = Bitmap.Config.ARGB_8888)
        .asImageBitmap()
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    val previousIndex = remember(this) {
        mutableIntStateOf(firstVisibleItemIndex)
    }

    val previousScrollOffset = remember {
        mutableIntStateOf(firstVisibleItemScrollOffset)
    }

    return remember(this) {
        derivedStateOf {
            if(previousIndex.value != firstVisibleItemIndex) {
                previousIndex.value > firstVisibleItemIndex
            } else {
                previousScrollOffset.value >= firstVisibleItemScrollOffset
            }.also {
                previousIndex.value = firstVisibleItemIndex
                previousScrollOffset.value = firstVisibleItemScrollOffset

            }
        }
    }.value
}

@Composable
fun createExtractorBrush(): Brush {
    val animation = rememberInfiniteTransition(label = "brush")
    val flat = with(LocalDensity.current) { 800.dp.toPx() }

    val offset by animation.animateFloat(
        initialValue = 0f,
        targetValue = flat,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brush"
    )

    val brush = Brush.linearGradient(
        listOf(
            md_theme_light_tertiary,
            md_theme_light_secondary,
        ),
        start = Offset(offset, offset),
        end = Offset(offset + flat, offset + flat),
        tileMode = TileMode.Mirror
    )

    return brush
}


fun Modifier.noRippleClickable(
    onClick: () -> Unit
) : Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}
    
