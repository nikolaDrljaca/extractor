package com.drbrosdev.extractor.framework.koin

import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
import org.koin.dsl.module

val workerModule = module {
    //Using named() as sometimes koin can fail to instantiate the workerParameters
    //leading to runtime exceptions
    worker(named<ExtractorWorker>()) {
        ExtractorWorker(
            context = androidContext(),
            workerParameters = it.get(),
            extractor = get(),
            mediaImageRepository = get<DefaultMediaStoreImageRepository>(),
            extractionDao = get(),
            notificationService = get()
        )
    }
    single { WorkManager.getInstance(androidContext()) }
}
