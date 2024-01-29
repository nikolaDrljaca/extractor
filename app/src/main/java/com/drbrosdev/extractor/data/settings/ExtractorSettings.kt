package com.drbrosdev.extractor.data.settings


data class ExtractorSettings(
    val shouldShowVisualAlbums: Boolean,
    val shouldShowTextAlbums: Boolean,
    val enableDynamicColors: Boolean
)

object ExtractorSettingsDefaults {
    const val showVisualDefault = true
    const val showTextDefault = true
    const val enableDynamicColors = false
}
