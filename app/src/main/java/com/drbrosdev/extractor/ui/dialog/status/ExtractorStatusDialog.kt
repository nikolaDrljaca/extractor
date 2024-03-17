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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.drbrosdev.extractor.ui.components.shared.ExtractorActionButton

@Composable
fun ExtractorStatusDialog(
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: ExtractorStatusDialogUiModel
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                    style = MaterialTheme.typography.displaySmall
                )
                IconButton(onClick = onCloseClick) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
                }
            }

            Text(
                text = stringResource(R.string.status_explanation),
                style = MaterialTheme.typography.labelMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            ExtractorCountChips(
                modifier = Modifier.fillMaxWidth(),
                inStorageCount = state.inStorageCount,
                onDeviceCount = state.onDeviceCount,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExtractorActionButton(
                onClick = onClick,
                enabled = state.shouldAllowExtraction,
                modifier = Modifier.fillMaxWidth()
            ) {
                with(state) {
                    when {
                        isExtractionRunning -> {
                            Text(text = state.percentageText)
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeCap = StrokeCap.Round,
                                trackColor = Color.Transparent,
                                color = Color.LightGray
                            )
                        }

                        onDeviceCount == 0 -> Text(text = stringResource(R.string.no_images_found))
                        onDeviceCount == inStorageCount -> Text(text = stringResource(R.string.all_done))
                        else -> Text(text = stringResource(id = R.string.start_sync))
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

