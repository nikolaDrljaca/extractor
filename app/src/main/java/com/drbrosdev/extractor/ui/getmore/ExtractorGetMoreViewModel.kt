package com.drbrosdev.extractor.ui.getmore

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import kotlinx.coroutines.launch

// TODO: wip
class ExtractorGetMoreViewModel(
    private val datastore: ExtractorDataStore
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    fun rewardPurchase() {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message = "", duration = SnackbarDuration.Long)
            datastore.incrementSearchCountBy(amount = 100)
        }
    }

    fun rewardSearches() {
        viewModelScope.launch {
            datastore.incrementSearchCountBy(amount = 100)
        }
    }
}