package com.drbrosdev.extractor.ui.settings

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.drbrosdev.extractor.framework.navigation.LocalNavController
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.ui.components.actionchips.AboutLink
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.ui.settings.bug.ExtractorFeedbackNavTarget
import com.drbrosdev.extractor.ui.settings.periodic.ExtractorPeriodicWorkNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
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
                when (it) {
                    AboutLink.FEEDBACK -> navController.navigate(ExtractorFeedbackNavTarget)
                    else -> Unit
                }
            },
            settingsState = viewModel.settingsState
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorSettingsScreen(
                onBack = { /*TODO*/ },
                onLicenseClick = {},
                onPeriodicSyncClick = { /*TODO*/ },
                onAboutLink = {},
                settingsState = ExtractorSettingsState(
                    initialEnabledVisual = false,
                    initialEnabledText = true,
                    initialEnableDynamicColor = false
                )
            )
        }
    }
}