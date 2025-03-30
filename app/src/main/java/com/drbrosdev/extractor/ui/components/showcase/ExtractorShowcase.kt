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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Embed
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.ExtractionData
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun ExtractorShowcase(
    modifier: Modifier = Modifier,
    extractionData: ExtractionData
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
            targetState = extractionData,
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
            ShowcaseItem(extractionData = it)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShowcaseItem(
    modifier: Modifier = Modifier,
    extractionData: ExtractionData
) {
    val background =
        Brush.verticalGradient(listOf(Color.Transparent, Color.Black))

    var opacity by rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(extractionData) {
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
            model = ImageRequest.Builder(LocalContext.current)
                .data(extractionData.extraction.uri.toUri())
                .crossfade(true)
                .build(),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.baseline_image_24),
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
                extractionData.visualEmbeds.forEach {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(it.value) },
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
                text = extractionData.textEmbed.value,
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
    val data = ExtractionData(
        extraction = Extraction(
            mediaImageId = MediaImageId(1L),
            uri = MediaImageUri(""),
            path = "",
            dateAdded = LocalDateTime.now(),
        ),
        textEmbed = Embed.Text("sample text here"),
        visualEmbeds = listOf(
            Embed.Visual("sample"),
            Embed.Visual("sample"),
            Embed.Visual("sample"),
            Embed.Visual("sample"),
        )
    )
    ExtractorTheme {
        ShowcaseItem(
            extractionData = data
        )
    }
}
