package com.drbrosdev.extractor.ui.components.usercollage

import androidx.compose.runtime.Immutable
import com.drbrosdev.extractor.domain.model.Extraction

// renders a list of extractions
@Immutable
data class ExtractionCollage(
    val keyword: String,
    val extractions: List<Extraction>
)