package com.drbrosdev.extractor.ui.root

import androidx.lifecycle.ViewModel
import com.drbrosdev.extractor.data.ExtractorDataStore

class RootViewModel(
    private val datastore: ExtractorDataStore
) : ViewModel() {

    fun isOnboardingFinished() = datastore.isOnboardingFinished
}