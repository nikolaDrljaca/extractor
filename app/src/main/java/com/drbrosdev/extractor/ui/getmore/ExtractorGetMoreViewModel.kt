package com.drbrosdev.extractor.ui.getmore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import kotlinx.coroutines.launch

class ExtractorGetMoreViewModel(
    private val datastore: ExtractorDataStore
) : ViewModel() {


    fun rewardSearches() {
        viewModelScope.launch {
            datastore.incrementSearchCount(amount = 100)
        }
    }
}