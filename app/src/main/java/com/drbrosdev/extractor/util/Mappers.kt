package com.drbrosdev.extractor.util

import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchTypeSwitchState


fun ExtractorSearchTypeSwitchState.Selection.toSearchType(): SearchType {
    return when (this) {
        ExtractorSearchTypeSwitchState.Selection.FULL -> SearchType.FULL
        ExtractorSearchTypeSwitchState.Selection.PARTIAL -> SearchType.PARTIAL
    }
}