package com.drbrosdev.extractor.domain.usecase

import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import kotlinx.coroutines.CoroutineDispatcher

class ResetIndex(
    private val dispatcher: CoroutineDispatcher,
    private val extractorRepository: ExtractorRepository,
    private val albumRepository: AlbumRepository
){

    /**
     * Will delete all search index and extraction data ->
     */
    suspend operator fun invoke() {

    }
}