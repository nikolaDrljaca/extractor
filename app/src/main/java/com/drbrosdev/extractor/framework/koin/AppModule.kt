package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.data.datastore
import com.drbrosdev.extractor.data.repository.DefaultExtractorDataRepository
import com.drbrosdev.extractor.data.repository.ExtractorDataRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

object CoroutineModuleName {
    const val IO = "IODispatcher"
    const val Default = "DefaultDispatcher"
}

private val coroutineModule = module {
    single(named(CoroutineModuleName.IO)) { Dispatchers.IO }
    single(named(CoroutineModuleName.Default)) { Dispatchers.Default }
}

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
        DefaultExtractorDataRepository(
            dispatcher = get(named(CoroutineModuleName.IO)),
            extractionEntityDao = get(),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            imageDataWithEmbeddingsDao = get()
        )
    } bind ExtractorDataRepository::class
}


val allKoinModules = listOf(
    dataModule,
    domainModule,
    coroutineModule,
    workerModule,
    viewModelModule,
    useCaseModule
)