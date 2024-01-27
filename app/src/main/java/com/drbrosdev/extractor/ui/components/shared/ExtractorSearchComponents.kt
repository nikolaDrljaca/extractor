package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorStillIndexing(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    contentColor: Color = Color.Gray
) {

    Box(modifier = Modifier.then(modifier), contentAlignment = contentAlignment) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.still_indexing_search_screen),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ExtractorEmptySearch(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Gray
) {
    Box(modifier = Modifier.then(modifier), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_hide_image_24),
                contentDescription = "",
                tint = contentColor,
                modifier = Modifier
                    .size(64.dp)
            )
            Text(
                text = stringResource(R.string.found_nothing),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(IntrinsicSize.Max),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExtractorStillIndexing()
            ExtractorEmptySearch()
        }
    }
}
