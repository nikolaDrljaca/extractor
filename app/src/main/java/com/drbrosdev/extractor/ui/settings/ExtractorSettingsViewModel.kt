package com.drbrosdev.extractor.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.settings.ExtractorSettingsDatastore
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
NOTE: This is an exceptional case where the ViewModel reaches into the data layer directly,
only because this is the single place where the mutation of settings values can take place
and another layer of abstraction here would prove unnecessary.
 */
class ExtractorSettingsViewModel(
    private val settingsDatastore: ExtractorSettingsDatastore
) : ViewModel() {
    val state = settingsDatastore.extractorSettings
        .map {
            ExtractorSettingsState(
                isDynamicColorEnabled = it.enableDynamicColors,
                onDynamicColorChanged = ::handleDynamicColorChange
            )
        }
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            ExtractorSettingsState(
                isDynamicColorEnabled = false,
                onDynamicColorChanged = {}
            )
        )

    private fun handleDynamicColorChange(value: Boolean) {
        viewModelScope.launch {
            settingsDatastore.setDynamicColor(value)
        }
    }
}