package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import com.drbrosdev.extractor.util.DialogNavTarget
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
            onClick = { viewModel.startExtractionSync() },
            headline = {
                Text(text = "Status", style = MaterialTheme.typography.displaySmall)
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
        )

        ExtractorStatusDialog(
            state = ExtractorStatusDialogUiModel(),
            onClick = {},
            headline = {
                Text(text = "Status", style = MaterialTheme.typography.displaySmall)
            }
        )
    }
}
