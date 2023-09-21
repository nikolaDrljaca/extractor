package com.drbrosdev.extractor.ui.components.previoussearch

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.PreviousSearch
import com.drbrosdev.extractor.ui.components.PreviousSearchItem
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviousSearches(
    onEvent: (PreviousSearchesEvents) -> Unit,
    modifier: Modifier = Modifier,
    searches: List<PreviousSearch>
) {

    LazyColumn(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(
                text = "Previous Searches",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        items(searches) {
            PreviousSearchItem(
                modifier = Modifier.animateItemPlacement(),
                text = it.query,
                count = it.resultCount,
                onClick = { onEvent(PreviousSearchesEvents.PerformSearch(it.query)) },
                onDelete = { onEvent(PreviousSearchesEvents.OnDeleteSearch(it)) }
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