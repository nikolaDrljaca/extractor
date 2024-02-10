package com.drbrosdev.extractor.framework.koin

import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.worker.ExtractorWorker
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker {
        ExtractorWorker(
            context = androidContext(),
            workerParameters = get(),
            extractor = get(),
            mediaImageRepository = get<DefaultMediaStoreImageRepository>(),
            extractionDao = get(),
            notificationService = get()
        )
    }
    single { WorkManager.getInstance(androidContext()) }
}
