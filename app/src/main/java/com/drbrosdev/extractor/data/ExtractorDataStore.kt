package com.drbrosdev.extractor.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "extractor_prefs")


class ExtractorDataStore(
    private val datastore: DataStore<Preferences>
) {
    val isOnboardingFinished: Flow<Boolean>
        get() = datastore.data.map {
            it[ONBOARDING_FINISHED] ?: false
        }

    suspend fun finishOnboarding() {
        datastore.edit {
            it[ONBOARDING_FINISHED] = true
        }
    }

    private companion object {
        val ONBOARDING_FINISHED = booleanPreferencesKey(name = "onb_fin")

    }
}