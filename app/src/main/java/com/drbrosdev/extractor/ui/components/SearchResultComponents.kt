package com.drbrosdev.extractor.ui.components

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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.MediaImage
import com.drbrosdev.extractor.ui.theme.ButtonShape


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
        ),
        shape = ButtonShape
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

@Composable
fun SearchFilterSheet(
    modifier: Modifier = Modifier,
    onClearFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Filters", style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(
                onClick = onClearFilterClick,
                border = BorderStroke(
                    width = 1.dp, color = Color.White
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Transparent
                ),
                shape = ButtonShape
            ) {
                Text(text = "1 applied", style = MaterialTheme.typography.labelMedium)
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.height(244.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DataFilter(
                onFilterChanged = {}
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
        }
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

