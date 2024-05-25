package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageFlowRow
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import java.time.LocalDateTime

@Composable
fun ExtractorUserCollageItem(
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
    onShareClick: () -> Unit,
    keyword: String,
    extractions: List<Extraction>,
) {
    Column(
        modifier = Modifier
            .then(modifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "# $keyword",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )

            AssistChip(
                modifier = Modifier
                    .padding(start = 8.dp),
                onClick = onShareClick,
                label = {
                    Text(text = stringResource(R.string.bottom_bar_share))
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
                },
                shape = CircleShape
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        ExtractorImageFlowRow(
            onClick = onItemClick,
            images = extractions,
            contentPadding = PaddingValues(0.dp),
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column {
                ExtractorUserCollageItem(
                    onItemClick = {},
                    onShareClick = {},
                    keyword = "sample",
                    extractions = listOf(
                        Extraction(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(1L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        Extraction(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(2L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        Extraction(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(3L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        Extraction(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(4L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                    ),
                )
            }
        }
    }
}