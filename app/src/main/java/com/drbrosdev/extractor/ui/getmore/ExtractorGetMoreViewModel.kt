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

    private val increaseAmount = 100
    val snackbarHostState = SnackbarHostState()

    fun rewardPurchase() {
        viewModelScope.launch {
            datastore.incrementSearchCountBy(amount = increaseAmount)
            snackbarHostState.showSnackbar(message = "", duration = SnackbarDuration.Long)
        }
    }

    fun rewardSearches() {
        viewModelScope.launch {
            datastore.incrementSearchCountBy(amount = increaseAmount)
        }
    }
}