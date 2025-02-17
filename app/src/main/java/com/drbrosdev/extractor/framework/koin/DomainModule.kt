package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.repository.ExtractorRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val domainModule = module {

    factory {
        DefaultExtractorRepository(
            dispatcher = get(named(CoroutineModuleName.IO)),
            extractionDao = get(),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            imageEmbeddingsDao = get(),
            searchIndexDao = get(),
            txRunner = get()
        )
    } bind ExtractorRepository::class

    factory {
        DefaultAlbumRepository(
            dispatcher = get(named(CoroutineModuleName.IO)),
            albumEntryDao = get(),
            albumConfigurationDao = get(),
            albumDao = get(),
            albumRelationDao = get(),
            runner = get()
        )
    } bind AlbumRepository::class
}
