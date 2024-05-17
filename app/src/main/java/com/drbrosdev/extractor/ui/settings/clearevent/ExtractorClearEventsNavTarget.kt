package com.drbrosdev.extractor.ui.settings.clearevent

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data object ExtractorClearEventsNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val navController = LocalNavController.current
        val viewModel: ExtractorClearEventsViewModel = koinViewModel()

        val eventCount by viewModel.eventCountState.collectAsStateWithLifecycle()

        ExtractorClearEventsScreen(
            onBack = { navController.pop() },
            onClearLogs = viewModel::clearEventLogs,
            eventCount = eventCount
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorClearEventsScreen(
                onBack = {},
                onClearLogs = {},
                eventCount = 54
            )
        }
    }
}

