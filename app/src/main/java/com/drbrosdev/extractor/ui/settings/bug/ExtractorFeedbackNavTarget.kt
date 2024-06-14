package com.drbrosdev.extractor.ui.settings.bug

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchEmailIntent
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
object ExtractorFeedbackNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorFeedbackViewModel = koinViewModel()

        val navController = navigators.navController
        val context = LocalContext.current

        CollectFlow(flow = viewModel.events) {
            when (it) {
                is ExtractorFeedbackEvents.SendEmail -> {
                    context.launchEmailIntent(it.content)
                }
            }
        }

        ExtractorFeedbackScreen(
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
            ExtractorFeedbackScreen(
                onBack = {},
                onSubmit = {},
                state = ExtractorFeedbackState("sample", false)
            )
        }
    }
}