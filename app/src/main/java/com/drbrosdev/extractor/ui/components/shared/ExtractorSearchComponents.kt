package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchView
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewState
import com.drbrosdev.extractor.ui.theme.ButtonShape
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


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
                onClick = { onClick(index) }
            )
        }
    }
}

@Composable
fun ExtractorSearchBottomSheet(
    onDone: () -> Unit,
    searchViewState: ExtractorSearchViewState,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Date Range",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            )
            BottomSheetButton(onClick = { /*TODO*/ }) {
                Text(text = "Select")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            )
            BottomSheetButton(onClick = { /*TODO*/ }) {
                Text(text = "Select")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun BottomSheetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    OutlinedButton(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp, color = Color.White
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        shape = ButtonShape
    ) {
        content()
    }
}

@Preview
@Composable
private fun SheetPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ExtractorSearchBottomSheet(
                onDone = {},
                searchViewState = ExtractorSearchViewState("", LabelType.ALL)
            )
        }
    }
}
