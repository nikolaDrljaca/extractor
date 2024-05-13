package com.drbrosdev.extractor.domain.usecase.image.search

import com.drbrosdev.extractor.domain.model.DateRange
import com.drbrosdev.extractor.domain.model.Extraction

interface SearchImageByDateRange {

    /**
     * Search all indexed images using just the [DateRange], independent of other query strings.
     */
    suspend fun execute(dateRange: DateRange): List<Extraction>
}