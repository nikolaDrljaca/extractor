package com.drbrosdev.extractor.ui.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorStatusDialog(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatusDialogUiModel
) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Status", style = MaterialTheme.typography.displaySmall)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.status_explanation),
                style = MaterialTheme.typography.labelMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            if (state.isExtractionRunning) {
                Text(text = "Current Extraction", modifier = Modifier.padding(vertical = 4.dp))
                LinearProgressIndicator(
                    progress = state.percentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            ExtractorCountChips(
                modifier = Modifier.fillMaxWidth(),
                inStorageCount = state.inStorageCount,
                onDeviceCount = state.onDeviceCount,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExtractorActionButton(
                onClick = onClick,
                enabled = !state.isExtractionRunning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start Sync")
            }

            AnimatedVisibility(
                visible = state.isExtractionRunning,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Process is already running.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
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
                contentColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "In Storage", style = MaterialTheme.typography.bodyMedium)
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
                Text(text = "On Device", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$onDeviceCount",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorStatusDialog(
            state = ExtractorStatusDialogUiModel(),
            onClick = {}
        )
    }
}