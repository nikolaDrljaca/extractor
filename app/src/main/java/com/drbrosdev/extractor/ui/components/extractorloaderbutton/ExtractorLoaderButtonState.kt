package com.drbrosdev.extractor.ui.components.extractorloaderbutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class ExtractorLoaderButtonState(
    initial: Target = Target.INITIAL,
    isEnabled: Boolean = true,
) {

    var current by mutableStateOf(initial)
        private set

    var isEnabled by mutableStateOf(true)
        private set

    fun loading() {
        current = Target.LOADING
    }

    fun success() {
        current = Target.SUCCESS
    }

    fun initial() {
        current = Target.INITIAL
    }

    fun disable() {
        isEnabled = false
    }

    fun enable() {
        isEnabled = true
    }

    enum class Target {
        INITIAL,
        LOADING,
        SUCCESS
    }

    companion object {
        val Saver = Saver<ExtractorLoaderButtonState, String>(
            save = {
                it.current.name
            },
            restore = {
                ExtractorLoaderButtonState(Target.valueOf(it))
            }
        )
    }
}

fun ExtractorLoaderButtonState.isSuccess(): Boolean {
    return current == ExtractorLoaderButtonState.Target.SUCCESS
}

@Composable
fun rememberExtractorLoaderButtonState(): ExtractorLoaderButtonState {
    return rememberSaveable(saver = ExtractorLoaderButtonState.Saver) {
        ExtractorLoaderButtonState()
    }
}
