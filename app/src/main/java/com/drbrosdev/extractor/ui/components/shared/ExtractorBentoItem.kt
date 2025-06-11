package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.ExtractionBundle
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.overview.OverviewGridState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

@Composable
fun ExtractorBentoItem(
    modifier: Modifier = Modifier,
    bundle: ExtractionBundle,
    overviewGridState: OverviewGridState,
    onClick: (keyword: String, index: Int) -> Unit,
) {
    val style = rememberSaveable(bundle.keyword) { BentoStyle.entries.toTypedArray().random() }
    Column(
        modifier = modifier
    ) {
        Text(
            text = bundle.keyword,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        BentoGrid(
            columns = 5,
            contentPadding = PaddingValues(
                top = 0.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp
            ),
        ) {
            items(bundle.extractions, style) { index, entry ->
                ExtractorImageItem(
                    cornerSize = 8.dp,
                    modifier = Modifier.padding(2.dp),
                    imageUri = entry.uri.toUri(),
                    size = 96,
                    onClick = {
                        if (overviewGridState.onToggleCheckedItem(entry.mediaImageId)) {
                            onClick(
                                bundle.keyword,
                                index
                            )
                        }
                    },
                    checkedState = overviewGridState.gridState[entry.mediaImageId],
                    onLongClick = {
                        overviewGridState.onLongTap(entry.mediaImageId)
                    }
                )
            }
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {

            ExtractorBentoItem(
                bundle = ExtractionBundle(
                    keyword = "sample",
                    extractions = emptyList()
                ),
                overviewGridState = OverviewGridState(),
                onClick = { _, _ -> }
            )
        }
    }
}