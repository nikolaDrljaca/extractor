package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorStatusDialogNavTarget : DialogNavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorStatusDialogViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        ExtractorStatusDialog(
            state = state,
            onClick = { viewModel.startExtraction() },
            headline = {
                Text(text = stringResource(R.string.status_dialog_status), style = MaterialTheme.typography.displaySmall)
            }
        )
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorStatusDialog(
            state = ExtractorStatusDialogUiModel(),
            onClick = {},
            headline = {
                Text(text = "Status", style = MaterialTheme.typography.displaySmall)
            }
        )
    }
}

@Preview
@Composable
private fun CurrentPreview2() {
    ExtractorTheme(dynamicColor = false) {
        val state = ExtractorStatusDialogUiModel(
            inStorageCount = 23,
            onDeviceCount = 162
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExtractorStatusDialog(
                state = state,
                onClick = {},
                headline = {
                    Text(text = "Status", style = MaterialTheme.typography.displaySmall)
                }
            )

            ExtractorStatusDialog(
                state = state.copy(isExtractionRunning = true),
                onClick = {},
                headline = {
                    Text(text = "Status", style = MaterialTheme.typography.displaySmall)
                }
            )
        }
    }
}
