package com.drbrosdev.extractor.domain.usecase.settings

import com.drbrosdev.extractor.data.settings.ExtractorSettingsDatastore
import com.drbrosdev.extractor.data.settings.ExtractorSettingsDefaults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ProvideMainActivitySettings(
    private val dispatcher: CoroutineDispatcher,
    private val settingsDatastore: ExtractorSettingsDatastore
) {

    operator fun invoke(): Flow<ExtractorMainActivitySettings> {
        return settingsDatastore.extractorSettings
            .map {
                ExtractorMainActivitySettings(it.enableDynamicColors)
            }
            .flowOn(dispatcher)
    }
}

data class ExtractorMainActivitySettings(
    val enableDynamicTheme: Boolean = ExtractorSettingsDefaults.ENABLE_DYNAMIC_COLORS
)