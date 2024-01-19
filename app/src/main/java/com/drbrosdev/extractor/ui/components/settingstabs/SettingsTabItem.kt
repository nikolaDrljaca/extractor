package com.drbrosdev.extractor.ui.components.settingstabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.drbrosdev.extractor.R


sealed class SettingsTabItem {
    data object Settings : SettingsTabItem()

    data object Licenses : SettingsTabItem()

    data object About : SettingsTabItem()
}

val tabItems = listOf(
    SettingsTabItem.Settings,
    SettingsTabItem.Licenses,
    SettingsTabItem.About,
)

@Composable
fun settingsItemAsString(item: SettingsTabItem): String {
    return when (item) {
        SettingsTabItem.About -> stringResource(id = R.string.settings_tab_about)
        SettingsTabItem.Licenses -> stringResource(id = R.string.settings_tab_licenses)
        SettingsTabItem.Settings -> stringResource(id = R.string.settings_tab_settings)
    }
}

