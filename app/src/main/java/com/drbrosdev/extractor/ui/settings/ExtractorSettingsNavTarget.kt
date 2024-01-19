package com.drbrosdev.extractor.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.launchViewIntent
import com.drbrosdev.extractor.util.showToast
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
object ExtractorSettingsNavTarget : NavTarget {

    @Composable
    override fun Content() {
        val viewModel: ExtractorSettingsViewModel = koinViewModel()

        val context = LocalContext.current
        val navController = LocalNavController.current

        ExtractorSettingsScreen(
            onBack = { navController.pop() },
            onLicenseClick = {
                context.launchViewIntent(it)
            },
            onConfigurePrecision = {
                context.showToast("Coming Soon")
            },
            onAboutLink = {
                //TODO
            },
            settingsState = viewModel.settingsState
        )
    }
}