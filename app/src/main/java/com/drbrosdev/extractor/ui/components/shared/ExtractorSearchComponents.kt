package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview

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
                contentDescription = "No result",
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

@Composable
fun ExtractorResetSearch(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(
            8.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExtractorTextButton(
            onClick = onClick,
        ) {
            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = stringResource(R.string.reset))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = stringResource(R.string.reset))
        }
        Text(
            text = stringResource(R.string.reload_suggestions),
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
        )
    }
}

@Composable
fun ExtractorGetMoreSearches(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.used_allocated_searches),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.searches_how_get_more),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedExtractorActionButton(onClick = onClick) {
            Text(text = stringResource(R.string.get_more))
        }
    }
}

@Composable
fun ExtractorSearchFabStack(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .then(modifier),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        SmallFloatingActionButton(
            onClick = onResetClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Refresh"
            )
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_save_24),
                contentDescription = "Save as album"
            )
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ExtractorStillIndexing()
                ExtractorEmptySearch()
                ExtractorResetSearch(onClick = { })
                ExtractorGetMoreSearches(onClick = { })
                ExtractorSearchFabStack(
                    onAddClick = { },
                    onResetClick = { }
                )
            }
        }
    }
}
