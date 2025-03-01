package com.drbrosdev.extractor.ui.components.usercollage

import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.ui.components.extractorimagegrid.ExtractorGridState

// TODO Domain model -- MOVE
data class ExtractionCollage(
    val keyword: String,
    val extractions: List<Extraction>
)

data class ExtractionCollageState(
    val keyword: String,
    val extractions: List<Extraction>,
    val gridState: ExtractorGridState = ExtractorGridState()
)