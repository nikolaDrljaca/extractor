package com.drbrosdev.extractor.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "extractor_prefs")

class ExtractorDataStore(
    private val datastore: DataStore<Preferences>
) {
    val isOnboardingFinished: Flow<Boolean>
        get() = datastore.data.map {
            it[ONBOARDING_FINISHED] ?: false
        }

    val searchCount: Flow<Int>
        get() = datastore.data.map {
            it[SEARCH_COUNTER] ?: 0
        }

    val showYourKeywordsBanner: Flow<Boolean>
        get() = datastore.data.map {
            it[SHOULD_SHOW_YOUR_KEYWORDS_BANNER] ?: true
        }

    suspend fun finishOnboarding() {
        datastore.edit {
            it[ONBOARDING_FINISHED] = true
        }
    }

    suspend fun incrementSearchCountBy(amount: Int = 1) {
        datastore.edit {
            val current = it[SEARCH_COUNTER] ?: 0
            it[SEARCH_COUNTER] = current.plus(amount)
        }
    }

    suspend fun decrementSearchCount() {
        datastore.edit {
            val current = it[SEARCH_COUNTER] ?: 0
            it[SEARCH_COUNTER] = current.minus(1)
        }
    }

    suspend fun getSearchCount(): Int = searchCount.first()

    suspend fun hasSeenYourKeywordsBanner() {
        datastore.edit {
            it[SHOULD_SHOW_YOUR_KEYWORDS_BANNER] = false
        }
    }

    private companion object {
        val ONBOARDING_FINISHED = booleanPreferencesKey(name = "onb_fin")
        val SEARCH_COUNTER = intPreferencesKey(name = "search_counter")
        val SHOULD_SHOW_YOUR_KEYWORDS_BANNER =
            booleanPreferencesKey(name = "show_your_keywords_banner")
    }
}