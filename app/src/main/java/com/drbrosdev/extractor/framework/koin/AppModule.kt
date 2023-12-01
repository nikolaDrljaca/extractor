package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.data.datastore
import com.drbrosdev.extractor.data.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.data.repository.ExtractorRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


private val dataModule = module {
    single { ExtractorDatabase.createExtractorDatabase(androidContext()) }

    single { get<ExtractorDatabase>().previousSearchDao() }
    single { get<ExtractorDatabase>().extractionEntityDao() }
    single { get<ExtractorDatabase>().imageDataWithEmbeddingsDao() }
    single { get<ExtractorDatabase>().textEmbeddingDao() }
    single { get<ExtractorDatabase>().visualEmbeddingDao() }
    single { get<ExtractorDatabase>().userEmbeddingDao() }

    factory {
        ExtractorDataStore(androidContext().datastore)
    }

    factory {
        DefaultExtractorRepository(
            dispatcher = get(named(CoroutineModuleName.IO)),
            extractionEntityDao = get(),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            imageDataWithEmbeddingsDao = get()
        )
    } bind ExtractorRepository::class
}


val allKoinModules = listOf(
    dataModule,
    domainModule,
    coroutineModule,
    workerModule,
    viewModelModule,
    useCaseModule
)