package com.drbrosdev.extractor.ui.components.extractorimagegrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.AlbumEntry
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextButton
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExtractorTextButton(
                    onClick = onReset,
                ) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.reset))
                }
                Text(
                    text = stringResource(R.string.reload_suggestions),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
        }
    }
}

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
    val paddingValues = PaddingValues(vertical = 112.dp)
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