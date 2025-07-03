package com.drbrosdev.extractor.ui.shop

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.framework.FeatureFlags
import com.drbrosdev.extractor.framework.check
import com.drbrosdev.extractor.util.WhileUiSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// TODO: wip
class ExtractorHubViewModel(
    private val datastore: ExtractorDataStore
) : ViewModel() {

    private val increaseAmount = 100
    val snackbarHostState = SnackbarHostState()

    val isSearchCountEnabled = FeatureFlags.SEARCH_COUNT_ENABLED.check()

    val searchCountState = datastore.searchCount
        .stateIn(
            viewModelScope,
            WhileUiSubscribed,
            0
        )

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