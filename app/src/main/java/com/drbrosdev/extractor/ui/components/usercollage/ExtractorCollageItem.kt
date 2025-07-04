package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.domain.model.LupaImageMetadata
import com.drbrosdev.extractor.domain.model.MediaImageId
import com.drbrosdev.extractor.domain.model.MediaImageUri
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorImageFlowRow
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import java.time.LocalDateTime

@Composable
fun ExtractorCollageItem(
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
    keyword: String,
    lupaImageMetadata: List<LupaImageMetadata>,
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        ExtractorImageFlowRow(
            modifier = Modifier.fillMaxWidth(),
            onClick = onItemClick,
            images = lupaImageMetadata,
        )
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column {
                ExtractorCollageItem(
                    onItemClick = {},
                    keyword = "sample",
                    lupaImageMetadata = listOf(
                        LupaImageMetadata(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(1L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        LupaImageMetadata(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(2L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        LupaImageMetadata(
                            uri = MediaImageUri(""),
                            mediaImageId = MediaImageId(3L),
                            path = "",
                            dateAdded = LocalDateTime.now()
                        ),
                        LupaImageMetadata(
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