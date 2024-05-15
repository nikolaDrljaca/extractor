package com.drbrosdev.extractor.ui.settings.index

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.showToast
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel


@Parcelize
data object ExtractorResetIndexNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val navController = LocalNavController.current
        val context = LocalContext.current

        val viewModel: ExtractorResetIndexViewModel = koinViewModel()

        val isActionLoading by viewModel.actionLoading.collectAsStateWithLifecycle()

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
