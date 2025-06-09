package com.drbrosdev.extractor.ui.components.albumoverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

private const val IMAGE_SIZE = 130

data class ExtractorAlbumOverview(
    val albumId: Long,
    val title: String,
    val searchType: String,
    val images: List<MediaImageUri>
) {
    val photoCount = images.count()
    val description = "$searchType \u00B7 $photoCount photos"
}

@Composable
fun ExtractorEmptyAlbumOverview(
    modifier: Modifier = Modifier,
    onInitClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .requiredHeight(IMAGE_SIZE.dp)
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onInitClick) {
            Icon(
                painterResource(id = R.drawable.round_image_search_24),
                contentDescription = "Image search"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.go_to_search))
        }
        Text(
            text = stringResource(R.string.create_own_album),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(IntrinsicSize.Max),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorAlbumOverviewContent(
    modifier: Modifier = Modifier,
    model: ExtractorAlbumOverview,
    onClick: () -> Unit,
) {
    val contentPadding = 8.dp
    val carouselState = rememberCarouselState { model.photoCount }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // title bar
            Column(
                modifier = Modifier.padding(contentPadding)
            ) {
                Text(
                    text = model.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = model.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // photo carousel
            HorizontalMultiBrowseCarousel(
                state = carouselState,
                preferredItemWidth = 92.dp,
                itemSpacing = 4.dp,
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp, bottom = 16.dp)
            ) { index ->
                val item = model.images[index]
                AsyncImage(
                    contentDescription = "",
                    modifier = Modifier
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .height(92.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.toUri())
                        .size(92 * 4)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.baseline_image_24),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    val model = ExtractorAlbumOverview(
        albumId = 1L,
        title = "Old friends in Paris",
        searchType = "Full",
        images = emptyList()
    )
    ExtractorTheme {
        ExtractorAlbumOverviewContent(
            model = model,
            onClick = {}
        )
    }
}
