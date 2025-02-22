package com.drbrosdev.extractor.domain.usecase.suggestion

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.extraction.dao.ExtractionDao
import com.drbrosdev.extractor.domain.model.SuggestedSearch

class GenerateSuggestedKeywords(
    private val extractionDao: ExtractionDao,
    private val dataStore: ExtractorDataStore,
    private val compileSearchSuggestions: CompileSearchSuggestions
) {
    suspend operator fun invoke(): Either<GenerateSuggestionsError, List<SuggestedSearch>> =
        either {
            ensure(dataStore.getSearchCount() != 0) {
                GenerateSuggestionsError.NoSearchesLeft
            }
            ensure(extractionDao.getCount() != 0) {
                GenerateSuggestionsError.NoExtractionsPresent
            }
            compileSearchSuggestions()
        }
}