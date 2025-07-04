package com.drbrosdev.extractor.ui.components.extractorimageitem

import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    size: Int? = null,
    cornerSize: Dp = 2.dp,
    checkedState: ExtractorListItemCheckedState
) {
    val sizeModifier = if (size != null) {
        Modifier.size(size.dp)
    } else {
        Modifier
    }
    val scaleSize = when {
        size != null -> size * 4
        else -> 200
    }

    val transition = updateTransition(targetState = checkedState, label = "")

    val cornerSizeAnimated by transition.animateDp(
        targetValueByState = {
            when (it) {
                ExtractorListItemCheckedState.CHECKED -> 14.dp
                ExtractorListItemCheckedState.UNCHECKED -> cornerSize
            }
        },
        label = ""
    )

    val scale by transition.animateFloat(
        targetValueByState = {
            when (it) {
                ExtractorListItemCheckedState.CHECKED -> 0.8f
                ExtractorListItemCheckedState.UNCHECKED -> 1f
            }
        },
        label = ""
    )

    val borderColor by transition.animateColor(
        targetValueByState = {
            when (it) {
                ExtractorListItemCheckedState.CHECKED -> MaterialTheme.colorScheme.primary
                ExtractorListItemCheckedState.UNCHECKED -> Color.Transparent
            }
        },
        label = ""
    )

    val cornerShape = RoundedCornerShape(cornerSizeAnimated)
    
    val haptics = LocalHapticFeedback.current

    AsyncImage(
        modifier = Modifier
            .then(sizeModifier)
            .then(modifier)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(cornerShape)
            .border(width = 4.dp, color = borderColor, shape = cornerShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .size(scaleSize, scaleSize)
            .crossfade(true)
            .build(),
        contentDescription = "Loaded image",
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.outline_hide_image_24)
    )
}


@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    onClick: () -> Unit,
    size: Int? = null,
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
            .then(modifier)
            .clip(RoundedCornerShape(2.dp))
            .clickable { onClick() },
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .size(scaleSize, scaleSize)
            .crossfade(true)
            .build(),
        contentDescription = "Loaded image",
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.baseline_image_24)
    )
}

@Composable
fun ExtractorImageItem(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    clipSize: Dp = 2.dp,
    size: Int? = null,
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
            .then(modifier)
            .clip(RoundedCornerShape(clipSize)),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .size(scaleSize, scaleSize)
            .crossfade(true)
            .build(),
        contentDescription = "Loaded image",
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.baseline_image_24)
    )
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            Row {
                repeat(4) {
                    val state = if (it % 2 == 0) ExtractorListItemCheckedState.CHECKED
                    else ExtractorListItemCheckedState.UNCHECKED

                    ExtractorImageItem(
                        imageUri = "".toUri(),
                        onClick = { },
                        onLongClick = {},
                        checkedState = state,
                        size = 200
                    )
                }
            }
        }
    }
}