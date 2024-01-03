package com.drbrosdev.extractor.ui.components.shared

import android.graphics.Bitmap
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButton
import com.drbrosdev.extractor.ui.components.extractorloaderbutton.ExtractorLoaderButtonState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.toUri


@Composable
fun ExtractorImageGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 112.dp),
    images: List<Extraction>,
    onClick: (index: Int) -> Unit,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val imageSize = 96

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Adaptive(minSize = imageSize.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(images, key = { _, it -> it.mediaImageId.id }) { index, it ->
            ExtractorImageItem(
                imageUri = it.uri.toUri(),
                size = imageSize,
                onClick = { onClick(index) },
            )
        }
    }
}

@Composable
fun ExtractorImageGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 112.dp),
    onClick: (index: Int) -> Unit,
    thumbnails: List<Bitmap>,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val imageSize = 96

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Adaptive(minSize = imageSize.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(thumbnails) { index, it ->
            ExtractorImageItem(
                bitmap = it,
                size = imageSize,
                onClick = { onClick(index) },
            )
        }
    }
}

@Composable
fun ExtractorSearchBottomSheet(
    onDone: () -> Unit,
    onCreateAlbumClick: () -> Unit,
    searchViewState: ExtractorSearchViewState,
    dateFilterState: ExtractorDateFilterState,
    loaderButtonState: ExtractorLoaderButtonState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExtractorSearchView(
            onDone = onDone,
            state = searchViewState,
            contentPadding = PaddingValues()
        )

        ExtractorDateFilter(state = dateFilterState)

        Spacer(modifier = Modifier.height(6.dp))

        ExtractorLoaderButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCreateAlbumClick,
            state = loaderButtonState,
            loadingContent = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    trackColor = Color.Transparent,
                    color = Color.Black,
                    strokeCap = StrokeCap.Round
                )
            },
            successContent = {
                Icon(imageVector = Icons.Rounded.Check, contentDescription = "")
                Text(text = stringResource(R.string.album_created))
            }
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "")
            Text(text = stringResource(R.string.create_album))
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun ExtractorFirstSearch(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Gray
) {
    val transition = rememberInfiniteTransition(label = "")

    val moveAnimation by transition.animateFloat(
        label = "",
        initialValue = 3f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
    )

    Box(modifier = Modifier.then(modifier), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.type_below_to_start_a_search),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
            )
            Icon(
                painter = painterResource(id = R.drawable.round_arrow_downward_24),
                contentDescription = "",
                tint = contentColor,
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        translationY = moveAnimation
                    }
            )
        }
    }
}

@Composable
fun ExtractorEmptySearch(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Gray
) {
    Box(modifier = Modifier.then(modifier), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_hide_image_24),
                contentDescription = "",
                tint = contentColor,
                modifier = Modifier
                    .size(64.dp)
            )
            Text(
                text = stringResource(R.string.found_nothing),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(IntrinsicSize.Max),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorFirstSearch()
            ExtractorEmptySearch()
        }
    }
}


@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchBottomSheet(
                onDone = {},
                onCreateAlbumClick = {},
                searchViewState = ExtractorSearchViewState("", LabelType.ALL),
                dateFilterState = ExtractorDateFilterState(),
                loaderButtonState = ExtractorLoaderButtonState()
            )
        }
    }
}