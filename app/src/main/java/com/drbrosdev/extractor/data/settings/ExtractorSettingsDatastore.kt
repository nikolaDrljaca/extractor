package com.drbrosdev.extractor.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map


val Context.settingsDatastore: DataStore<Preferences> by preferencesDataStore(name = "extractor_settings")

class ExtractorSettingsDatastore(
    private val settingsDatastore: DataStore<Preferences>
) {
    private val showVisualAlbums: Flow<Boolean>
        get() = settingsDatastore.data.map {
            it[SHOW_VISUAL] ?: ExtractorSettingsDefaults.SHOW_VISUAL_DEFAULT
        }

    private val showTextAlbums: Flow<Boolean>
        get() = settingsDatastore.data.map {
            it[SHOW_TEXT] ?: ExtractorSettingsDefaults.SHOW_TEXT_DEFAULT
        }

    private val enableDynamicColorsFlow: Flow<Boolean>
        get() = settingsDatastore.data.map {
            it[ENABLE_DYNAMIC_COLOR] ?: ExtractorSettingsDefaults.ENABLE_DYNAMIC_COLORS
        }

    val extractorSettings: Flow<ExtractorSettings> = combine(
        showVisualAlbums,
        showTextAlbums,
        enableDynamicColorsFlow
    ) { showVisual, showText, enableDynamic ->
        ExtractorSettings(
            shouldShowVisualAlbums = showVisual,
            shouldShowTextAlbums = showText,
            enableDynamicColors = enableDynamic
        )
    }

    suspend fun setShowVisualAlbums(value: Boolean) {
        settingsDatastore.edit {
            it[SHOW_VISUAL] = value
        }
    }

    suspend fun setShowTextAlbums(value: Boolean) {
        settingsDatastore.edit {
            it[SHOW_TEXT] = value
        }
    }

    suspend fun setDynamicColor(value: Boolean) {
        settingsDatastore.edit {
            it[ENABLE_DYNAMIC_COLOR] = value
        }
    }

    private companion object {
        val SHOW_VISUAL = booleanPreferencesKey(name = "show_visual")
        val SHOW_TEXT = booleanPreferencesKey(name = "show_text")
        val ENABLE_DYNAMIC_COLOR = booleanPreferencesKey(name = "enable_dynamic_color")
    }
}



