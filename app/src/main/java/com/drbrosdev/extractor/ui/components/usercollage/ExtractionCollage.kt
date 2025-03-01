package com.drbrosdev.extractor.ui.components.usercollage

import com.drbrosdev.extractor.domain.model.Extraction

// TODO Domain model -- MOVE
data class ExtractionCollage(
    val keyword: String,
    val extractions: List<Extraction>
)