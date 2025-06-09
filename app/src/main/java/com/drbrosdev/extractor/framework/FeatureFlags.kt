package com.drbrosdev.extractor.framework

import com.drbrosdev.extractor.BuildConfig


enum class FeatureFlags(
    // tags correspond to names of buildConfigField values in build.gradle
    val tag: String
) {
    SEARCH_COUNT_ENABLED("SEARCH_COUNT_ENABLED")
}

fun FeatureFlags.check(): Boolean = when (this) {
    FeatureFlags.SEARCH_COUNT_ENABLED -> BuildConfig.SEARCH_COUNT_ENABLED
}