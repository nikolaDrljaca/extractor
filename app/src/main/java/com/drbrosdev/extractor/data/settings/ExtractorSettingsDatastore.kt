package com.drbrosdev.extractor.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.settingsDatastore: DataStore<Preferences> by preferencesDataStore(name = "extractor_settings")

class ExtractorSettingsDatastore(
    private val settingsDatastore: DataStore<Preferences>
) {
    private val enableDynamicColorsFlow: Flow<Boolean>
        get() = settingsDatastore.data.map {
            it[ENABLE_DYNAMIC_COLOR] ?: ExtractorSettingsDefaults.ENABLE_DYNAMIC_COLORS
        }

    val extractorSettings: Flow<ExtractorSettings> = enableDynamicColorsFlow
        .map { enableDynamic ->
            ExtractorSettings(
                enableDynamicColors = enableDynamic
            )
        }

    suspend fun setDynamicColor(value: Boolean) {
        settingsDatastore.edit {
            it[ENABLE_DYNAMIC_COLOR] = value
        }
    }

    private companion object {
        val ENABLE_DYNAMIC_COLOR = booleanPreferencesKey(name = "enable_dynamic_color")
    }
}



