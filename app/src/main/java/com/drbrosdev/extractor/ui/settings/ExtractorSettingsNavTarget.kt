package com.drbrosdev.extractor.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.ui.components.extractorsettings.rememberExtractorSettingsState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.LocalNavController
import com.drbrosdev.extractor.util.NavTarget
import com.drbrosdev.extractor.util.ScreenPreview
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

@Parcelize
object ExtractorSettingsNavTarget : NavTarget {

    @Composable
    override fun Content() {

        val navController = LocalNavController.current

        ExtractorSettingsScreen(
            onBack = { navController.pop() },
            settingsState = rememberExtractorSettingsState(
                initialEnabledText = false,
                initialEnabledVisual = true
            )
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ExtractorSettingsScreen(
                onBack = {},
                settingsState = ExtractorSettingsState(
                    initialEnabledVisual = false,
                    initialEnabledText = true
                )
            )
        }
    }
}