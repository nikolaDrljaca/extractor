package com.drbrosdev.extractor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.MediaImage


@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowLeft,
            contentDescription = "Go back",
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Text(
            text = "Back",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun ExtractorImageGrid(
    modifier: Modifier = Modifier,
    images: List<MediaImage>,
    onClick: () -> Unit,
    gridState: LazyGridState = rememberLazyGridState()
) {

    LazyVerticalGrid(
        modifier = Modifier
            .then(modifier),
        columns = GridCells.Fixed(count = 3),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item(key = "spacer") {
            Spacer(modifier = Modifier.height(140.dp))
        }

        item(
            span = { GridItemSpan(maxLineSpan) },
            key = "search_term"
        ) {
            Text(
                text = "Search Term",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
            )
        }

        itemsIndexed(images, key = { _, it -> it.id }) { _, it ->
            ExtractorImageItem(
                imageUri = it.uri,
                size = 144,
                onClick = onClick
            )
        }
    }
}
