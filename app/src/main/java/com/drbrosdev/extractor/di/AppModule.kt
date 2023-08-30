package com.drbrosdev.extractor.di

import androidx.work.WorkManager
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.data.datastore
import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.usecase.DefaultImageSearch
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.ui.home.HomeViewModel
import com.drbrosdev.extractor.ui.image.ImageDetailViewModel
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerViewModel
import com.drbrosdev.extractor.ui.result.SearchResultViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
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
    single { get<ExtractorDatabase>().imageDataDao() }
    factory {
        ExtractorDataStore(androidContext().datastore)
    }
}


private val workerModule = module {
    worker {
        ExtractorWorker(
            context = androidContext(),
            workerParameters = get(),
            extractor = get(),
            mediaImageRepository = get<DefaultMediaImageRepository>(),
            imageDataDao = get()
        )
    }
    single { WorkManager.getInstance(androidContext()) }
}

private val uiModule = module {
    viewModel {
        StartWorkerViewModel(
            workManager = get(),
            datastore = get(),
        )
    }

    viewModel {
        RootViewModel(
            datastore = get()
        )
    }

    viewModel {
        HomeViewModel(
            imageDataDao = get(),
            mediaImageRepository = get<DefaultMediaImageRepository>(),
            imageSearch = get<DefaultImageSearch>(),
        )
    }

    viewModel {
        ImageDetailViewModel(
            mediaImageRepository = get<DefaultMediaImageRepository>()
        )
    }

    viewModel {
        SearchResultViewModel(
            imageSearch = get<DefaultImageSearch>()
        )
    }
}

val allKoinModules = listOf(dataModule, domainModule, coroutineModule, workerModule, uiModule)