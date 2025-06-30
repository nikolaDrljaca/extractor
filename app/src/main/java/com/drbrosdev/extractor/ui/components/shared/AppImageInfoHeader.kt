package com.drbrosdev.extractor.ui.components.shared

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.imageinfo.LupaImageHeaderState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun AppImageInfoHeader(
    modifier: Modifier = Modifier,
    model: LupaImageHeaderState
) {
    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .height(144.dp)
                .clip(RoundedCornerShape(28.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.uri.toUri())
                .size(192 * 2)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.baseline_image_24),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Image Info",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "# ID: ${model.mediaImageId}"
            )
            Text(
                text = model.dateAdded,
                color = Color.Gray
            )
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            AppImageInfoHeader(
                model = LupaImageHeaderState(
                    mediaImageId = 12123123,
                    uri = Uri.EMPTY.toString(),
                    dateAdded = "2025-01-01"
                )
            )
        }
    }
}