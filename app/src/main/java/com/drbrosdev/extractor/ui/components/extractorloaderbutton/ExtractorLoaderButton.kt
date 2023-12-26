package com.drbrosdev.extractor.ui.components.extractorloaderbutton

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.shared.ExtractorButtonDefaults
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview


@Composable
fun ExtractorLoaderButton(
    onClick: () -> Unit,
    state: ExtractorLoaderButtonState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ExtractorButtonDefaults.paddingValues(),
    colors: ButtonColors = ExtractorLoaderButtonDefaults.colors(
        containerColor = Color.White,
        contentColor = Color.Black
    ),
    loadingContent: @Composable (RowScope.() -> Unit),
    successContent: @Composable (RowScope.() -> Unit),
    content: @Composable (RowScope.() -> Unit),
) {

    Button(
        onClick = {
            when (state.current) {
                ExtractorLoaderButtonState.Target.INITIAL -> onClick()
                else -> Unit
            }
        },
        modifier = Modifier
            .then(modifier),
        colors = colors,
        enabled = enabled,
        contentPadding = contentPadding,
        shape = RoundedCornerShape(18.dp)
    ) {
        AnimatedContent(
            targetState = state.current,
            label = "",
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 220,
                        delayMillis = 90
                    )
                ) togetherWith fadeOut(animationSpec = tween(durationMillis = 90))
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                when (it) {
                    ExtractorLoaderButtonState.Target.INITIAL -> content()
                    ExtractorLoaderButtonState.Target.LOADING -> loadingContent()
                    ExtractorLoaderButtonState.Target.SUCCESS -> successContent()
                }
            }
        }
    }
}

object ExtractorLoaderButtonDefaults {
    private val leftContentPadding = ButtonDefaults.ContentPadding.calculateLeftPadding(
        LayoutDirection.Ltr
    )

    private val rightContentPadding = ButtonDefaults.ContentPadding.calculateRightPadding(
        LayoutDirection.Ltr
    )

    @Composable
    fun paddingValues(vertical: Dp = 16.dp): PaddingValues = PaddingValues(
        start = leftContentPadding,
        end = rightContentPadding,
        top = vertical,
        bottom = vertical
    )

    @Composable
    fun colors(
        containerColor: Color,
        contentColor: Color
    ): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContentColor = Color.LightGray,
        disabledContainerColor = Color.Gray
    )
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorLoaderButton(
            onClick = { /*TODO*/ },
            state = ExtractorLoaderButtonState(),
            content = { Text(text = "Loader") },
            loadingContent = {},
            successContent = {}
        )
    }
}


