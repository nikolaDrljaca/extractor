package com.drbrosdev.extractor.ui.components.previoussearch

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.PreviousSearchItem
import com.drbrosdev.extractor.ui.components.PreviousSearchItemViewType
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviousSearches(
    onEvent: (PreviousSearchesEvents) -> Unit,
    modifier: Modifier = Modifier,
    searches: List<PreviousSearchItemState>
) {

    LazyColumn(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        item {
            Text(
                text = "Previous Searches",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        itemsIndexed(searches) { index, item ->
            PreviousSearchItem(
                modifier = Modifier.animateItemPlacement(),
                itemState = item,
                onClick = {
                    onEvent(
                        PreviousSearchesEvents.PerformSearch(
                            query = item.text,
                            labelType = item.labelType
                        )
                    )
                },
                onDelete = { onEvent(PreviousSearchesEvents.OnDeleteSearch(item)) },
                viewType = {
                    when {
                        searches.size == 1 -> PreviousSearchItemViewType.NEUTRAL
                        index == 0 -> PreviousSearchItemViewType.FIRST
                        index == searches.size - 1 -> PreviousSearchItemViewType.LAST
                        else -> PreviousSearchItemViewType.NEUTRAL
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        PreviousSearches(onEvent = {}, searches = emptyList())
    }
}