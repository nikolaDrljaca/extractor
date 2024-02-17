package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import com.drbrosdev.extractor.ui.components.shared.ExtractorResetSearch
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.toUri

@Composable
fun ExtractorImageGrid(
    onClick: (index: Int) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ExtractorImageGridDefaults.paddingValues,
    images: List<Extraction>,
    state: ExtractorImageGridState
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
        itemsIndexed(images, key = { _, it -> it.mediaImageId.id }) { index, it ->
            ExtractorImageItem(
                imageUri = it.uri.toUri(),
                size = imageSize,
                onClick = {
                    val handled = state.onItemClick(index)
                    if (!handled) onClick(index)
                },
                checkedState = state[index],
                onLongClick = { state.onItemLongClick(index) }
            )
        }

        item(
            key = "reset",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            ExtractorResetSearch(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorImageGrid(
    onClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ExtractorImageGridDefaults.paddingValues,
    albumEntries: List<AlbumEntry>,
    state: ExtractorImageGridState
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
                modifier = Modifier.animateItemPlacement(),
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
    val paddingValues = PaddingValues(vertical = 116.dp)
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorImageGrid(
                images = emptyList(),
                onClick = {},
                onReset = { /*TODO*/ },
                state = ExtractorImageGridState()
            )
        }
    }
}