package com.drbrosdev.extractor.ui.settings

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.actionchips.AboutLink
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.ui.settings.bug.ExtractorFeedbackNavTarget
import com.drbrosdev.extractor.ui.settings.clearevent.ExtractorClearEventsNavTarget
import com.drbrosdev.extractor.ui.settings.index.ExtractorResetIndexNavTarget
import com.drbrosdev.extractor.ui.settings.periodic.ExtractorPeriodicWorkNavTarget
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.ScreenPreview
import com.drbrosdev.extractor.util.launchPlayStorePage
import com.drbrosdev.extractor.util.launchPrivacyPolicyIntent
import com.drbrosdev.extractor.util.launchShareAppIntent
import com.drbrosdev.extractor.util.launchViewIntent
import com.drbrosdev.extractor.util.launchWebpageIntent
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
object ExtractorSettingsNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorSettingsViewModel = koinViewModel()

        val context = LocalContext.current
        val navController = navigators.navController

        val settingsState = viewModel.state.collectAsStateWithLifecycle()

        ExtractorSettingsScreen(
            onBack = { navController.pop() },
            onLicenseClick = {
                context.launchViewIntent(it)
            },
            onPeriodicSyncClick = {
                navController.navigate(ExtractorPeriodicWorkNavTarget)
            },
            onAboutLink = {
                when (it) {
                    AboutLink.FEEDBACK -> navController.navigate(ExtractorFeedbackNavTarget)
                    AboutLink.WEBSITE -> context.launchWebpageIntent()
                    AboutLink.POLICY -> context.launchPrivacyPolicyIntent()
                    AboutLink.SHARE -> context.launchShareAppIntent()
                    AboutLink.RATE -> context.launchPlayStorePage()
                }
            },
            onClearEventLogs = {
                navController.navigate(ExtractorClearEventsNavTarget)
            },
            onResetIndex = {
                navController.navigate(ExtractorResetIndexNavTarget)
            },
            settingsState = settingsState.value
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface {
            ExtractorSettingsScreen(
                onBack = { },
                onLicenseClick = {},
                onPeriodicSyncClick = { },
                onResetIndex = {},
                onAboutLink = {},
                onClearEventLogs = {},
                settingsState = ExtractorSettingsState(
                    isDynamicColorEnabled = false,
                    onDynamicColorChanged = {}
                )
            )
        }
    }
}