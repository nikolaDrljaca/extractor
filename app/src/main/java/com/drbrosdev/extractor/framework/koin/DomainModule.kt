package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {

    factory {
        DefaultMediaImageRepository(
            contentResolver = androidContext().contentResolver,
            dispatcher = get(named(CoroutineModuleName.IO))
        )
    } bind MediaImageRepository::class

}
