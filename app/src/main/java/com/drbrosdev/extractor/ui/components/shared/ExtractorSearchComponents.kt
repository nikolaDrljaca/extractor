package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.LabelType
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilter
import com.drbrosdev.extractor.ui.components.extractordatefilter.ExtractorDateFilterState
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorImageGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 112.dp),
    images: List<MediaImage>,
    onClick: (index: Int) -> Unit,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val imageSize = 86

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Adaptive(minSize = imageSize.dp),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(images, key = { _, it -> it.mediaImageId }) { index, it ->
            ExtractorImageItem(
                imageUri = it.uri,
                size = imageSize,
                onClick = { onClick(index) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
fun ExtractorSearchBottomSheet(
    onDone: () -> Unit,
    searchViewState: ExtractorSearchViewState,
    dateFilterState: ExtractorDateFilterState,
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

        FilterRow(onClick = { /*TODO*/ }, text = "Location")

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun ColumnScope.FilterRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        )
        BottomSheetButton(onClick = onClick) {
            Text(text = "Select")
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
                searchViewState = ExtractorSearchViewState("", LabelType.ALL),
                dateFilterState = ExtractorDateFilterState()
            )
        }
    }
}