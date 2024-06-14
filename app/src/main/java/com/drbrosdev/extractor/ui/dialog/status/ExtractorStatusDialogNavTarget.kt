package com.drbrosdev.extractor.ui.dialog.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.framework.navigation.DialogNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.CombinedPreview
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorStatusDialogNavTarget : DialogNavTarget {

    @Composable
    override fun Content(navController: NavController<DialogNavTarget>) {
        val viewModel: ExtractorStatusDialogViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorStatusDialogEvents.CloseDialog -> navController.pop()
            }
        }

        ExtractorStatusDialog(
            state = state,
            onCloseClick = {
                navController.pop()
            }
        )
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        ExtractorStatusDialog(
            state = ExtractorStatusDialogUiState.Done(
                122, 122, {}
            ),
            onCloseClick = {}
        )
    }
}

@Preview
@Composable
private fun CurrentPreview2() {
    ExtractorTheme(dynamicColor = false) {
        val state = ExtractorStatusDialogUiState.InProgress(
            inStorageCount = 23,
            onDeviceCount = 162,
            eventSink = {}
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExtractorStatusDialog(
                state = state,
                onCloseClick = {}
            )

            ExtractorStatusDialog(
                state = state,
                onCloseClick = {}
            )
        }
    }
}
