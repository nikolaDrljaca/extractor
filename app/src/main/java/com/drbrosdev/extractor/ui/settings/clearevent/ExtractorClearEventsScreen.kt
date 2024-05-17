package com.drbrosdev.extractor.ui.settings.clearevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton


@Composable
fun ExtractorClearEventsScreen(
    onBack: () -> Unit,
    onClearLogs: () -> Unit,
    eventCount: Int = 0,
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            // put content under top bar, not great
            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "Clear Event Logs",
                style = MaterialTheme.typography.headlineMedium
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_release_alert_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.this_is_a_destructive_action),
                    modifier = Modifier.weight(1f),
                    style = textStyle.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }

            Text(
                text = "Clear the application event log. Event logs are stored locally and describe things that happen while you use the app.\nThis can be very helpful to the developers when the app crashes or does something unexpected. Whilst they should not take up too much space, you still have the control to delete them all here, if you wish.",
                style = textStyle,
                modifier = Modifier.padding(
                    vertical = 8.dp
                )
            )

            Text(
                text = "Logs stored: $eventCount",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedExtractorActionButton(
                onClick = onClearLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Text(text = "Delete Event Logs")
            }
        }

        ExtractorTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            leadingSlot = {
                BackIconButton(onBack = onBack)
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
    }
}