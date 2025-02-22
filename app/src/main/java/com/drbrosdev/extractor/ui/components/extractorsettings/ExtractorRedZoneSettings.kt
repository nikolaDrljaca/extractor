package com.drbrosdev.extractor.ui.components.extractorsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorRedZoneSettings(
    modifier: Modifier = Modifier,
    onResetIndex: () -> Unit,
    onClearEventLogs: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 4.dp),
        modifier = Modifier
            .then(modifier)
    ) {
        ExtractorSettingsItem(
            itemPosition = ExtractorSettingsItemPosition.FIRST,
            onClick = onResetIndex,
            trailingSlot = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.reset_index)
                )
            },
            contentColor = MaterialTheme.colorScheme.error,
        ) {
            Text(text = stringResource(R.string.reset_index))
        }

        ExtractorSettingsItem(
            itemPosition = ExtractorSettingsItemPosition.LAST,
            onClick = onClearEventLogs,
            trailingSlot = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.clear_event_logs)
                )
            },
            contentColor = MaterialTheme.colorScheme.error,
        ) {
            Text(text = stringResource(R.string.clear_event_logs))
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorRedZoneSettings(
            onResetIndex = {},
            onClearEventLogs = {}
        )
    }
}