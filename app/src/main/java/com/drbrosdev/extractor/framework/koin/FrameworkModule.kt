package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.MediaStoreImageRepository
import com.drbrosdev.extractor.framework.StringResourceProvider
import com.drbrosdev.extractor.framework.logger.EventLogDatabase
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.framework.notification.NotificationService
import com.drbrosdev.extractor.framework.permission.PermissionService
import org.koin.android.ext.koin.androidContext
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

    single {
        NotificationService(androidContext())
    }

    single {
        PermissionService(androidContext())
    }

    single { EventLogDatabase.createLogDatabase(androidContext()) }
    single { get<EventLogDatabase>().eventLogDao() }

}
