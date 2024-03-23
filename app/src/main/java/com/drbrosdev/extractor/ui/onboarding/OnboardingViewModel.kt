package com.drbrosdev.extractor.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val spawnExtractorWork: SpawnExtractorWork,
    private val datastore: ExtractorDataStore,
) : ViewModel() {

    fun spawnWorkRequest() {
        spawnExtractorWork()
    }

    fun finishOnboarding() {
        viewModelScope.launch { datastore.finishOnboarding() }
    }
}