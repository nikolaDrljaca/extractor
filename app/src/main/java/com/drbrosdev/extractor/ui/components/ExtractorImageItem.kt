package com.drbrosdev.extractor.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R

@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    size: Int? = null,
    onClick: () -> Unit
) {
    val sizeModifier = if (size != null) {
        Modifier.size(size.dp)
    } else {
        Modifier
    }

    AsyncImage(
        modifier = Modifier
            .then(sizeModifier)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .then(modifier),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.baseline_image_24)
    )
}