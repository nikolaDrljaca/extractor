package com.drbrosdev.extractor.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.domain.usecase.CompleteOnboarding
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val completeOnboarding: CompleteOnboarding
) : ViewModel() {

    fun finishOnboarding() {
        viewModelScope.launch {
            completeOnboarding.invoke()
        }
    }
}