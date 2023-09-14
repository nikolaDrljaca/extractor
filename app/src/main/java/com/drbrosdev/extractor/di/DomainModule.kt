package com.drbrosdev.extractor.di

import com.drbrosdev.extractor.data.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.repository.MediaImageRepository
import com.drbrosdev.extractor.domain.usecase.BulkExtractor
import com.drbrosdev.extractor.domain.usecase.DefaultExtractor
import com.drbrosdev.extractor.domain.usecase.DefaultImageSearch
import com.drbrosdev.extractor.domain.usecase.DefaultInputImageProvider
import com.drbrosdev.extractor.domain.usecase.Extractor
import com.drbrosdev.extractor.domain.usecase.ImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.ImageSearch
import com.drbrosdev.extractor.domain.usecase.InputImageProvider
import com.drbrosdev.extractor.domain.usecase.InsertPreviousSearch
import com.drbrosdev.extractor.domain.usecase.MLKitImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.MlKitTextExtractor
import com.drbrosdev.extractor.domain.usecase.TextExtractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factory { DefaultInputImageProvider(context = androidContext()) } bind InputImageProvider::class

    factory {
        MLKitImageLabelExtractor(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind ImageLabelExtractor::class

    factory {
        MlKitTextExtractor(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind TextExtractor::class

    factory {
        DefaultExtractor(
            labelExtractor = get(),
            textExtractor = get(),
            provider = get(),
            dispatcher = get(named(CoroutineModuleName.Default)),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            extractorEntityDao = get()
        )
    } bind Extractor::class

    factory {
        DefaultMediaImageRepository(contentResolver = androidContext().contentResolver)
    } bind MediaImageRepository::class

    factory {
        BulkExtractor(
            dispatcher = get(named(CoroutineModuleName.Default)),
            mediaImageRepository = get(),
            extractorRepository = get<DefaultExtractorRepository>(),
            extractor = get()
        )
    }

    factory {
        InsertPreviousSearch(
            dispatcher = get(named(CoroutineModuleName.IO)),
            dao = get()
        )
    }

    factory {
        DefaultImageSearch(
            dispatcher = get(named(CoroutineModuleName.IO)),
            mediaImageRepository = get(),
            imageDataWithEmbeddingsDao = get(),
            insertPreviousSearch = get()
        )
    } bind ImageSearch::class

}
