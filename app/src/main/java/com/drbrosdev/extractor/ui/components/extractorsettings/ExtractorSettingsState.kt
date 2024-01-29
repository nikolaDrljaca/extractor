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
    initialEnabledText: Boolean,
    initialEnableDynamicColor: Boolean
) {
    var enabledVisualAlbums by mutableStateOf(initialEnabledVisual)
        private set

    var enabledTextAlbums by mutableStateOf(initialEnabledText)
        private set

    var enableDynamicColor by mutableStateOf(initialEnableDynamicColor)
        private set

    fun updateEnabledVisualAlbums(value: Boolean) {
        enabledVisualAlbums = value
    }

    fun updateEnabledTextAlbums(value: Boolean) {
        enabledTextAlbums = value
    }

    fun updateEnableDynamicColor(value: Boolean) {
        enableDynamicColor = value
    }

    object Saver :
        androidx.compose.runtime.saveable.Saver<ExtractorSettingsState, Map<String, Boolean>> {
        override fun restore(value: Map<String, Boolean>): ExtractorSettingsState {
            return ExtractorSettingsState(
                initialEnabledVisual = value.getOrDefault("visualAlbums", false),
                initialEnabledText = value.getOrDefault("textAlbums", false),
                initialEnableDynamicColor = value.getOrDefault("enableDynamic", false)
            )
        }

        override fun SaverScope.save(value: ExtractorSettingsState): Map<String, Boolean> {
            return mapOf(
                "textAlbums" to value.enabledTextAlbums,
                "visualAlbums" to value.enabledVisualAlbums,
                "enableDynamic" to value.enableDynamicColor
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

fun ExtractorSettingsState.enableDynamicColorAsFlow(): Flow<Boolean> {
    return snapshotFlow { enableDynamicColor }
}

@Composable
fun rememberExtractorSettingsState(
    initialEnabledText: Boolean,
    initialEnabledVisual: Boolean,
    initialEnableDynamicColor: Boolean
): ExtractorSettingsState {
    return rememberSaveable(
        saver = ExtractorSettingsState.Saver
    ) {
        ExtractorSettingsState(
            initialEnabledVisual = initialEnabledVisual,
            initialEnabledText = initialEnabledText,
            initialEnableDynamicColor = initialEnableDynamicColor
        )
    }
}
