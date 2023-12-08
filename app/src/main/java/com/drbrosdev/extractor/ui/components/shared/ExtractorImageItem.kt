package com.drbrosdev.extractor.ui.components.shared

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.util.shimmerBrush

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
    val scaleSize = when {
        size != null -> size * 2
        else -> 200
    }

    AsyncImage(
        modifier = Modifier
            .then(sizeModifier)
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .then(modifier),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .size(scaleSize, scaleSize)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.baseline_image_24)
    )
}

@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    byteArray: ByteArray,
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
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .then(modifier),
        model = ImageRequest.Builder(LocalContext.current)
            .data(byteArray)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.baseline_image_24)
    )
}


@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    size: Int? = null,
    onClick: () -> Unit
) {
    val sizeModifier = if (size != null) {
        Modifier.size(size.dp)
    } else {
        Modifier
    }

    Image(
        modifier = Modifier
            .then(sizeModifier)
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .then(modifier),
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ExtractorImagePlaceholder(
    modifier: Modifier = Modifier,
    size: Int? = null,
) {
    val shimmerBrush = shimmerBrush()
    val sizeModifier = if (size != null) {
        Modifier.size(size.dp)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .then(sizeModifier)
            .clip(RoundedCornerShape(8.dp))
            .background(brush = shimmerBrush)
            .then(modifier)
    )
}