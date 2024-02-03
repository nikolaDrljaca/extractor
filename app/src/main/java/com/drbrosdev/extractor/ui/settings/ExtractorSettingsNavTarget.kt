package com.drbrosdev.extractor.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.settings.periodic.ExtractorPeriodicWorkNavTarget
import com.drbrosdev.extractor.util.launchViewIntent
import dev.olshevski.navigation.reimagined.navigate
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
            onPeriodicSyncClick = {
                navController.navigate(ExtractorPeriodicWorkNavTarget)
            },
            onAboutLink = {
                //TODO
            },
            settingsState = viewModel.settingsState
        )
    }
}