package com.drbrosdev.extractor.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import kotlinx.coroutines.flow.flow

fun processedImages() = flow {
    val items = setOf(
        "content://media/external/images/media/39",
        "content://media/external/images/media/40",
        "content://media/external/images/media/41",
        "content://media/external/images/media/42",
        "content://media/external/images/media/43",
        "content://media/external/images/media/44",
        "content://media/external/images/media/45",
        "content://media/external/images/media/46",
        "content://media/external/images/media/47",
        "content://media/external/images/media/48",
    )
    items.forEach {
        emit(MediaImageUri(it))
        kotlinx.coroutines.delay(3_500)
    }
}

const val CONTENT_ANIMATION_DURATION = 1_000

@Composable
fun ExtractorShowcase(
    modifier: Modifier = Modifier,
//    extractionData: ExtractionData
    mediaImageUri: MediaImageUri
) {
    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        Text("Indexing is in progress.")

        AnimatedContent(
//            targetState = extractionData,
            targetState = mediaImageUri,
            modifier = Modifier,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                val enter = slideInHorizontally(
                    animationSpec = tween(CONTENT_ANIMATION_DURATION),
                    initialOffsetX = { fullWidth -> fullWidth }
                )
                val exit = slideOutHorizontally(
                    animationSpec = tween(CONTENT_ANIMATION_DURATION),
                    targetOffsetX = { fullWidth -> -fullWidth }
                )
                enter togetherWith exit
            }
        ) {
//            ShowcaseItem(extractionData = it)
            ShowcaseItem(mediaImageUri = it)
        }
    }
}

@Composable
private fun ShowcaseItem(
    modifier: Modifier = Modifier,
//    extractionData: ExtractionData
    mediaImageUri: MediaImageUri
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .then(modifier)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
        ) {
            // image
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight(0.67f),
                model = ImageRequest.Builder(LocalContext.current)
//                    .data(extractionData.extraction.uri.toUri())
                    .data(mediaImageUri.toUri())
                    .crossfade(true)
                    .build(),
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
            )
        }

        // bottom "bar" data
        val background = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
        Box(
            modifier = Modifier
                .background(background)
                .padding(12.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(
//                text = extractionData.textEmbed.value,
                text = "some random text",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
