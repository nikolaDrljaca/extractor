package com.drbrosdev.extractor.data.settings


data class ExtractorSettings(
    val shouldShowVisualAlbums: Boolean,
    val shouldShowTextAlbums: Boolean,
    val enableDynamicColors: Boolean
)

object ExtractorSettingsDefaults {
    const val SHOW_VISUAL_DEFAULT = true
    const val SHOW_TEXT_DEFAULT = true
    const val ENABLE_DYNAMIC_COLORS = false
}
