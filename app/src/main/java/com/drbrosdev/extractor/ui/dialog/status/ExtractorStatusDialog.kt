package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton

@Composable
fun ExtractorStatusDialog(
    modifier: Modifier = Modifier,
    state: ExtractorStatusDialogUiState
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.status_dialog_status),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Text(
                text = stringResource(R.string.status_explanation),
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.padding(vertical = 12.dp))

            ExtractorCountChips(
                modifier = Modifier.fillMaxWidth(),
                inStorageCount = state.inStorageCount,
                onDeviceCount = state.onDeviceCount,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExtractorButton(
                onClick = { state.eventSink() },
                modifier = Modifier.fillMaxWidth()
            ) {
                when (state) {
                    is ExtractorStatusDialogUiState.CanStart -> Text(text = stringResource(id = R.string.start_sync))
                    is ExtractorStatusDialogUiState.Done -> Text(text = stringResource(id = R.string.all_done))
                    is ExtractorStatusDialogUiState.InProgress -> {
                        Text(text = state.percentageText)
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeCap = StrokeCap.Round,
                            trackColor = Color.Transparent,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.dialog_work_background),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )

            Text(
                text = stringResource(R.string.in_storage_lower),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )
        }
    }
}


@Composable
private fun ExtractorCountChips(
    modifier: Modifier = Modifier,
    onDeviceCount: Int,
    inStorageCount: Int
) {
    Row(
        modifier = Modifier
            .then(modifier)
    ) {
        Card(
            shape = RoundedCornerShape(
                topEnd = 0.dp,
                bottomEnd = 0.dp,
                topStart = 14.dp,
                bottomStart = 14.dp
            ),
            border = BorderStroke(width = 1.dp, color = Color.Gray),
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.status_in_storage),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$inStorageCount",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 14.dp,
                bottomEnd = 14.dp
            ),
            border = BorderStroke(width = 1.dp, color = Color.Gray),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.status_on_device),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$onDeviceCount",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

    }
}

