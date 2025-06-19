package com.drbrosdev.extractor.ui.components.showcase

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.components.recommendsearch.LupaImageHighlight
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.asImageRequest
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun ExtractorShowcase(
    modifier: Modifier = Modifier,
    highlight: LupaImageHighlight
) {
    Column(
        modifier = Modifier
            .then(modifier),
    ) {
        Text(
            text = stringResource(R.string.indexing_is_in_progress),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = highlight,
            modifier = Modifier,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val enter = slideInHorizontally(
                    animationSpec =
                        tween(ExtractorShowcaseDefaults.IMAGE_TRANSITION_DURATION),
                    initialOffsetX = { fullWidth -> fullWidth }
                )
                val exit = slideOutHorizontally(
                    animationSpec =
                        tween(ExtractorShowcaseDefaults.IMAGE_TRANSITION_DURATION),
                    targetOffsetX = { fullWidth -> -fullWidth }
                )
                enter togetherWith exit
            }
        ) {
            ShowcaseItem(highlight = it)
        }
    }
}

@Composable
private fun ShowcaseItem(
    modifier: Modifier = Modifier,
    highlight: LupaImageHighlight
) {
    val background =
        Brush.verticalGradient(listOf(Color.Transparent, Color.Black))

    var opacity by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(highlight) {
        delay(ExtractorShowcaseDefaults.EMBED_ALPHA_DELAY.milliseconds)
        // opacity from 0 to 1
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween()
        ) { value, _ -> opacity = value }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(12.dp)
            .clip(RoundedCornerShape(14.dp))
            .then(modifier)
    ) {
        // image
        AsyncImage(
            modifier = Modifier
                .matchParentSize()
                .align(Alignment.Center),
            model = highlight.lupaImageMetadata.uri.asImageRequest(LocalContext.current),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
        )

        // bottom "bar" data
        Column(
            modifier = Modifier
                .graphicsLayer {
                    alpha = opacity
                }
                .background(background)
                .padding(12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .align(Alignment.BottomCenter)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                highlight.visualEmbeds.forEach {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(it) },
                        enabled = false,
                        shape = CircleShape,
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true
                        ),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            disabledLabelColor = Color.White,
                        )
                    )
                }
            }

            Text(
                text = highlight.textEmbed,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 4.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.Gray
                )
            )
        }
    }
}

object ExtractorShowcaseDefaults {
    const val IMAGE_TRANSITION_DURATION = 1_000

    const val EMBED_ALPHA_DELAY = 500

    const val SHOWCASE_SAMPLE_RATE = 3_500L
}

@Preview
@Composable
private fun CurrentPreview() {
    val data = LupaImageHighlight(
        lupaImageMetadata = LupaImageMetadata(
            mediaImageId = MediaImageId(1L),
            uri = MediaImageUri(""),
            path = "",
            dateAdded = LocalDateTime.now(),
        ),
        textEmbed = "sample text here",
        visualEmbeds = listOf(
            "sample",
            "sample",
            "sample",
            "sample",
        )
    )
    ExtractorTheme {
        ShowcaseItem(
            highlight = data
        )
    }
}
