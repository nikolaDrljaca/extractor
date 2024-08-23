package com.drbrosdev.extractor.domain.usecase.settings

import com.drbrosdev.extractor.data.settings.ExtractorSettingsDatastore
import com.drbrosdev.extractor.data.settings.ExtractorSettingsDefaults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case to provide home screen specific settings.
 *
 * @param settingsDatastore Global settings store.
 * @return [ExtractorHomeScreenSettings]
 */
class ProvideHomeScreenSettings(
    private val dispatcher: CoroutineDispatcher,
    private val settingsDatastore: ExtractorSettingsDatastore
) {

    operator fun invoke(): Flow<ExtractorHomeScreenSettings> {
        return settingsDatastore.extractorSettings
            .map {
                ExtractorHomeScreenSettings(
                    shouldShowVisualAlbums = it.shouldShowVisualAlbums,
                    shouldShowTextAlbums = it.shouldShowTextAlbums
                )
            }
    }
}


/**
 * Settings values to be used by the home screen.
 */
data class ExtractorHomeScreenSettings(
    val shouldShowVisualAlbums: Boolean = ExtractorSettingsDefaults.SHOW_VISUAL_DEFAULT,
    val shouldShowTextAlbums: Boolean = ExtractorSettingsDefaults.SHOW_TEXT_DEFAULT,
)
