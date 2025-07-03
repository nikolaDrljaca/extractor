package com.drbrosdev.extractor.domain.usecase.suggestion

sealed interface GenerateSuggestionsError {

    data object NoExtractionsPresent : GenerateSuggestionsError

    data object NoSearchesLeft : GenerateSuggestionsError

}