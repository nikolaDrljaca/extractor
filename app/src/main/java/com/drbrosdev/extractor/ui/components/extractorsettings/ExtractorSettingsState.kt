package com.drbrosdev.extractor.ui.components.extractorsettings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow

@Stable
class ExtractorSettingsState(
    initialEnabledVisual: Boolean,
    initialEnabledText: Boolean
) {
    var enabledVisualAlbums by mutableStateOf(initialEnabledVisual)
        private set

    var enabledTextAlbums by mutableStateOf(initialEnabledText)
        private set

    fun updateEnabledVisualAlbums(value: Boolean) {
        enabledVisualAlbums = value
    }

    fun updateEnabledTextAlbums(value: Boolean) {
        enabledTextAlbums = value
    }

    object Saver :
        androidx.compose.runtime.saveable.Saver<ExtractorSettingsState, Map<String, Boolean>> {
        override fun restore(value: Map<String, Boolean>): ExtractorSettingsState {
            return ExtractorSettingsState(
                initialEnabledVisual = value.getOrDefault("visualAlbums", false),
                initialEnabledText = value.getOrDefault("textAlbums", false)
            )
        }

        override fun SaverScope.save(value: ExtractorSettingsState): Map<String, Boolean> {
            return mapOf(
                "textAlbums" to value.enabledTextAlbums,
                "visualAlbums" to value.enabledVisualAlbums
            )
        }
    }
}

fun ExtractorSettingsState.visualEnabledAsFlow(): Flow<Boolean> {
    return snapshotFlow { enabledVisualAlbums }
}

fun ExtractorSettingsState.textEnabledAsFlow(): Flow<Boolean> {
    return snapshotFlow { enabledTextAlbums }
}

@Composable
fun rememberExtractorSettingsState(
    initialEnabledText: Boolean,
    initialEnabledVisual: Boolean
): ExtractorSettingsState {
    return rememberSaveable(
        saver = ExtractorSettingsState.Saver
    ) {
        ExtractorSettingsState(initialEnabledVisual, initialEnabledText)
    }
}
