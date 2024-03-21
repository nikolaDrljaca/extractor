package com.drbrosdev.extractor.ui.settings.bug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchEmailIntent
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorBugReportNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorBugReportViewModel = koinViewModel()

        val navController = LocalNavController.current
        val context = LocalContext.current

        CollectFlow(flow = viewModel.events) {
            when (it) {
                is ExtractorBugReportEvents.SendEmail -> {
                    context.launchEmailIntent(it.content)
                }
            }
        }

        ExtractorBugReportScreen(
            onBack = {
                navController.pop()
            },
            onSubmit = {
                viewModel.onSubmitFeedback()
            },
            state = viewModel.state
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            ExtractorBugReportScreen(
                onBack = {},
                onSubmit = {},
                state = ExtractorBugReportState("sample", false)
            )
        }
    }
}