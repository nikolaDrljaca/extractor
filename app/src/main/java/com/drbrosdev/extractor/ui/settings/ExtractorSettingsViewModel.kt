package com.drbrosdev.extractor.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.settings.ExtractorSettingsDatastore
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsState
import com.drbrosdev.extractor.ui.components.extractorsettings.enableDynamicColorAsFlow
import com.drbrosdev.extractor.ui.components.extractorsettings.textEnabledAsFlow
import com.drbrosdev.extractor.ui.components.extractorsettings.visualEnabledAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/*
NOTE: This is an exceptional case where the ViewModel reaches into the data layer directly,
only because this is the single place where the mutation of settings values can take place
and another layer of abstraction here would prove unnecessary.
 */
class ExtractorSettingsViewModel(
    private val settingsDatastore: ExtractorSettingsDatastore
) : ViewModel() {

    val settingsState = ExtractorSettingsState(
        initialEnabledVisual = false,
        initialEnabledText = false,
        initialEnableDynamicColor = false
    )

    private val datastoreJob = settingsDatastore.extractorSettings
        .distinctUntilChanged()
        .onEach {
            settingsState.updateEnabledTextAlbums(it.shouldShowTextAlbums)
            settingsState.updateEnabledVisualAlbums(it.shouldShowVisualAlbums)
            settingsState.updateEnableDynamicColor(it.enableDynamicColors)
        }
        .launchIn(viewModelScope)

    private val updateVisualJob = settingsState.visualEnabledAsFlow()
        .onEach { settingsDatastore.setShowVisualAlbums(it) }
        .launchIn(viewModelScope)

    private val updateTextJob = settingsState.textEnabledAsFlow()
        .onEach { settingsDatastore.setShowTextAlbums(it) }
        .launchIn(viewModelScope)

    private val updateDynamicColor = settingsState.enableDynamicColorAsFlow()
        .onEach { settingsDatastore.setDynamicColor(it) }
        .launchIn(viewModelScope)
}