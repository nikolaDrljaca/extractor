package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultLupaImageRepository
import com.drbrosdev.extractor.domain.repository.AlbumRepository
import com.drbrosdev.extractor.domain.repository.LupaImageRepository
import org.koin.dsl.bind
import org.koin.dsl.module


val domainModule = module {

    factory {
        DefaultLupaImageRepository(
            extractionDao = get(),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            descriptionEmbeddingDao = get(),
            userEmbeddingDao = get(),
            imageEmbeddingsDao = get(),
            searchIndexDao = get(),
            txRunner = get()
        )
    } bind LupaImageRepository::class

    factory {
        DefaultAlbumRepository(
            albumEntryDao = get(),
            albumConfigurationDao = get(),
            albumDao = get(),
            albumRelationDao = get(),
            runner = get()
        )
    } bind AlbumRepository::class
}
