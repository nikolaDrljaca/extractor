package com.drbrosdev.extractor.di

import com.drbrosdev.extractor.data.ExtractorDatabase
import com.drbrosdev.extractor.domain.usecase.DefaultExtractor
import com.drbrosdev.extractor.domain.usecase.DefaultInputImageProvider
import com.drbrosdev.extractor.domain.usecase.Extractor
import com.drbrosdev.extractor.domain.usecase.ImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.InputImageProvider
import com.drbrosdev.extractor.domain.usecase.MLKitImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.MlKitTextExtractor
import com.drbrosdev.extractor.domain.usecase.TextExtractor
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

object CoroutineModuleName {
    const val IO = "IODispatcher"
    const val Default = "DefaultDispatcher"
}

val coroutineModule = module {
    single(named(CoroutineModuleName.IO)) { Dispatchers.IO }
    single(named(CoroutineModuleName.Default)) { Dispatchers.Default }
}

val dataModule = module {
    single { ExtractorDatabase.createExtractorDatabase(androidContext()) }
}

val domainModule = module {
    factory { DefaultInputImageProvider(androidContext()) } bind InputImageProvider::class
    factory { MLKitImageLabelExtractor(get(named(CoroutineModuleName.Default))) } bind ImageLabelExtractor::class
    factory { MlKitTextExtractor(get(named(CoroutineModuleName.Default))) } bind TextExtractor::class
    factoryOf(::DefaultExtractor) bind Extractor::class
}