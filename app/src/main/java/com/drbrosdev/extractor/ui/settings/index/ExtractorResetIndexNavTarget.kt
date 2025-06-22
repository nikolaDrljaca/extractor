package com.drbrosdev.extractor.ui.settings.index

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.showToast
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
data object ExtractorResetIndexNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val navController = navigators.navController
        val context = LocalContext.current

        val viewModel: ExtractorResetIndexViewModel = koinViewModel()

        val isActionLoading by viewModel.loading.collectAsStateWithLifecycle()

        CollectFlow(flow = viewModel.events) {
            when (it) {
                ExtractorResetIndexEvents.WorkerStarted -> context.showToast("Indexing started.")
            }
        }

        ExtractorResetIndexScreen(
            onBack = { navController.pop() },
            onResetIndex = viewModel::resetImageIndex,
            isLoading = isActionLoading
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = true) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorResetIndexScreen(
                onBack = {  },
                onResetIndex = {  },
                isLoading = false
            )
        }
    }
}
