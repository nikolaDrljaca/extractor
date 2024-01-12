package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.TransactionProvider
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.data.datastore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { ExtractorDatabase.createExtractorDatabase(androidContext()) }

    single { get<ExtractorDatabase>().previousSearchDao() }
    single { get<ExtractorDatabase>().extractionEntityDao() }
    single { get<ExtractorDatabase>().imageDataWithEmbeddingsDao() }
    single { get<ExtractorDatabase>().textEmbeddingDao() }
    single { get<ExtractorDatabase>().visualEmbeddingDao() }
    single { get<ExtractorDatabase>().userEmbeddingDao() }
    single { get<ExtractorDatabase>().albumDao() }
    single { get<ExtractorDatabase>().albumEntryDao() }
    single { get<ExtractorDatabase>().albumConfigurationDao() }
    single { get<ExtractorDatabase>().albumRelationDao() }

    single {
        TransactionProvider(
            database = get<ExtractorDatabase>()
        )
    }

    factory {
        ExtractorDataStore(androidContext().datastore)
    }
}
