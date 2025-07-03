package com.drbrosdev.extractor.ui.components.extractorsettings

import androidx.compose.runtime.Immutable

@Immutable
data class ExtractorSettingsState(
    val isDynamicColorEnabled: Boolean,
    val onDynamicColorChanged: (Boolean) -> Unit
)