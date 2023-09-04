package com.drbrosdev.extractor.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RootViewModel(
    private val datastore: ExtractorDataStore
) : ViewModel() {

    val isOnboardingFinished = datastore
        .isOnboardingFinished
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}