package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.toUri

@Composable
fun ExtractorUserCollageThumbnail(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageUri: MediaImageUri,
    keywords: String
) {
    val shape = RoundedCornerShape(12.dp)
    val sizeModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
    val scaleSize = 460

    Box(
        modifier = Modifier
            .then(modifier)
            .clip(shape)
            .then(sizeModifier)
    ) {
        AsyncImage(
            modifier = Modifier
                .then(sizeModifier)
                .clip(shape)
                .clickable { onClick() },
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri.toUri())
                .size(scaleSize, scaleSize)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.baseline_image_24)
        )

        Box(
            modifier = Modifier
                .background(color = Color.Black.copy(alpha = 0.25f))
                .matchParentSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.your_keywords),
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
            )

            Text(
                text = keywords,
                modifier = Modifier,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column {
                ExtractorUserCollageThumbnail(
                    modifier = Modifier.padding(12.dp),
                    onClick = { },
                    imageUri = MediaImageUri(""),
                    keywords = "sample, user, food"
                )
            }
        }
    }
}