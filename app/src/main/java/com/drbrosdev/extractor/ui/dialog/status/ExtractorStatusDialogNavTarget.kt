package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R
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
            onClick = { viewModel.startExtraction() },
            headline = {
                Text(text = stringResource(R.string.status_dialog_status), style = MaterialTheme.typography.displaySmall)
            }
        )
    }
}

