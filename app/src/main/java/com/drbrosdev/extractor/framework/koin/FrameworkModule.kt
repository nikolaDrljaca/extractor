package com.drbrosdev.extractor.framework.koin

import androidx.work.WorkManager
import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.domain.service.AppReviewService
import com.drbrosdev.extractor.domain.service.ExtractorWorkerService
import com.drbrosdev.extractor.domain.service.InferenceService
import com.drbrosdev.extractor.framework.ActivityProvider
import com.drbrosdev.extractor.framework.PlayAppReviewService
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.framework.logger.EventLogDatabase
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.framework.mlkit.MlKitMediaPipeInferenceService
import com.drbrosdev.extractor.framework.notification.NotificationService
import com.drbrosdev.extractor.framework.permission.PermissionService
import com.drbrosdev.extractor.framework.workmanager.AlbumCleanupWorker
import com.drbrosdev.extractor.framework.workmanager.DefaultExtractorWorkerService
import com.drbrosdev.extractor.framework.workmanager.ExtractorWorker
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkModule = module {

    factory {
        DefaultMediaStoreImageRepository(
            contentResolver = androidContext().contentResolver,
            dispatcher = get(named(CoroutineModuleName.IO))
        )
    } bind MediaStoreImageRepository::class

    single {
        StringResourceProvider(
            context = androidContext()
        )
    }

    single { WorkManager.getInstance(androidContext()) }

    single {
        NotificationService(androidContext())
    }

    single {
        PermissionService(androidContext())
    }

    single {
        PlayAppReviewService(
            context = androidApplication(),
            activityProvider = get()
        )
    } bind AppReviewService::class

    single { ActivityProvider() }

    single {
        DefaultExtractorWorkerService(
            workManager = get()
        )
    } bind ExtractorWorkerService::class

    single {
        MlKitMediaPipeInferenceService(
            dispatcher = get(named(CoroutineModuleName.Default)),
            context = androidContext()
        )
    } bind InferenceService::class

    //NOTE: Using named() as sometimes koin can fail to instantiate the workerParameters
    //leading to runtime exceptions
    worker(named<ExtractorWorker>()) {
        ExtractorWorker(
            context = androidContext(),
            workerParameters = it.get(),
            notificationService = get()
        )
    }
    worker(named<AlbumCleanupWorker>()) {
        AlbumCleanupWorker(
            context = androidContext(),
            workerParameters = it.get(),
            cleanupAlbum = get()
        )
    }

    single { EventLogDatabase.createLogDatabase(androidContext()) }
    single { get<EventLogDatabase>().eventLogDao() }

}
