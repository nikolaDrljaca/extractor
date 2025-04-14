package com.drbrosdev.extractor.util

import android.content.Context
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.theme.md_theme_light_secondary
import com.drbrosdev.extractor.ui.theme.md_theme_light_tertiary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun applicationIconResource(): Painter {
    return painterResource(id = R.drawable.ic_launcher)
}

fun MediaImageUri.asImageRequest(context: Context) = ImageRequest.Builder(context)
    .data(uri)
    .memoryCacheKey(uri)
    .diskCacheKey(uri)
    .crossfade(true)
    .build()

fun Uri.asImageRequest(context: Context) = ImageRequest.Builder(context)
    .data(toString())
    .memoryCacheKey(toString())
    .diskCacheKey(toString())
    .build()

fun Modifier.shimmer(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
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

enum class KeyboardState {
    VISIBLE,
    HIDDEN
}

@Composable
fun rememberKeyboardState(): State<KeyboardState> {
    val isImeVisible = if (WindowInsets.ime.getBottom(LocalDensity.current) > 0) {
        KeyboardState.VISIBLE
    } else {
        KeyboardState.HIDDEN
    }
    return rememberUpdatedState(isImeVisible)
}

fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
fun <T> CollectFlow(
    flow: Flow<T>,
    action: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(action)
        }
    }
}

fun <T> StateFlow<T>.asState(scope: CoroutineScope): State<T> {
    val internalState = mutableStateOf(value)
    scope.launch {
        this@asState.collect {
            internalState.value = it
        }
    }
    return internalState
}

fun LazyGridScope.maxLineSpanItem(
    key: Any? = null,
    content: @Composable LazyGridItemScope.() -> Unit
) = item(span = { GridItemSpan(maxLineSpan) }, key = key) {
    content()
}

@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    var previousIndex by rememberSaveable(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by rememberSaveable(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
