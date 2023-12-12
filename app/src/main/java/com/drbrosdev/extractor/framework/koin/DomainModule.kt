package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
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
            imageEmbeddingsDao = get()
        )
    } bind ExtractorRepository::class
}
