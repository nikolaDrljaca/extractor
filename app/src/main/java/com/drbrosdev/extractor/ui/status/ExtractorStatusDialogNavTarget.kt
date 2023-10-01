package com.drbrosdev.extractor.ui.status

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            onClick = { viewModel.startExtractionSync() }
        )
    }
}