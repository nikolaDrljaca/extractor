package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.toUri

@Composable
fun ExtractorImageGrid(
    onClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ExtractorImageGridDefaults.paddingValues,
    gridState: ExtractorGridState,
    images: List<Extraction>,
) {
    val imageSize = 96

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Adaptive(minSize = imageSize.dp),
        state = gridState.lazyGridState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(images, key = { _, it -> it.mediaImageId.id }) { index, it ->
            ExtractorImageItem(
                imageUri = it.uri.toUri(),
                size = imageSize,
                onClick = {
                    val handled = gridState.onItemClick(index)
                    if (!handled) onClick(index)
                },
                checkedState = gridState[index],
                onLongClick = { gridState.onItemLongClick(index) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractorImageFlowRow(
    onClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    images: List<Extraction>,
) {
    val imageSize = 96

    FlowRow(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        images.forEachIndexed { index, it ->
            ExtractorImageItem(
                modifier = Modifier,
                imageUri = it.uri.toUri(),
                size = imageSize,
                onClick = { onClick(index) },
            )
        }
    }
}

@Composable
fun ExtractorImageGrid(
    onClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ExtractorImageGridDefaults.paddingValues,
    albumEntries: List<AlbumEntry>,
    state: ExtractorGridState
) {
    val imageSize = 96

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Adaptive(minSize = imageSize.dp),
        state = state.lazyGridState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            albumEntries,
            key = { _, it -> it.id.id }
        ) { index, entry ->
            ExtractorImageItem(
                modifier = Modifier.animateItem(),
                imageUri = entry.uri.toUri(),
                size = imageSize,
                onClick = {
                    val handled = state.onItemClick(index)
                    if (!handled) onClick(index)
                },
                checkedState = state[index],
                onLongClick = { state.onItemLongClick(index) }
            )
        }
    }
}

object ExtractorImageGridDefaults {
    val paddingValues = PaddingValues(vertical = 120.dp)
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorImageGrid(
                images = emptyList(),
                onClick = {},
                gridState = ExtractorGridState()
            )
        }
    }
}