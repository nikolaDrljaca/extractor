package com.drbrosdev.extractor.ui.components.previoussearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.ui.components.shared.PreviousSearchItem
import com.drbrosdev.extractor.ui.components.shared.PreviousSearchItemViewType
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun PreviousSearches(
    onEvent: (PreviousSearchesEvents) -> Unit,
    modifier: Modifier = Modifier,
    searches: List<PreviousSearchItemState>
) {

    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
            Text(
                text = "Previous Searches",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

         Spacer(modifier = Modifier.height(8.dp))

        searches.forEachIndexed { index, item ->
            PreviousSearchItem(
                modifier = Modifier,
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