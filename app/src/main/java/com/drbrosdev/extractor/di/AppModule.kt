package com.drbrosdev.extractor.di

import androidx.work.WorkManager
import com.drbrosdev.extractor.MainViewModel
import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.domain.usecase.DefaultExtractor
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
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
}


private val workerModule = module {
    worker {
        ExtractorWorker(androidContext(), get(), get())
    }
    single { WorkManager.getInstance(androidContext()) }
}

private val uiModule = module {
    viewModel {
        MainViewModel(
            extractor = get<DefaultExtractor>(),
            workManager = get(),
            imageSearch = get()
        )
    }
}

val allKoinModules = listOf(dataModule, domainModule, coroutineModule, workerModule, uiModule)