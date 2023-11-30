package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.RememberSearch
import com.drbrosdev.extractor.domain.usecase.extractor.DefaultExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor
import com.drbrosdev.extractor.domain.usecase.image.create.DefaultInputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.image.search.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.label.extractor.ImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.label.extractor.MLKitImageLabelExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.MlKitTextExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.TextExtractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val useCaseModule = module {

    factory {
        DefaultInputImageFactory(context = androidContext())
    } bind InputImageFactory::class

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
            inputImageFactory = get(),
            dispatcher = get(named(CoroutineModuleName.Default)),
            extractorRepository = get()
        )
    } bind Extractor::class

    factory {
        BulkExtractor(
            dispatcher = get(named(CoroutineModuleName.Default)),
            mediaImageRepository = get(),
            extractorDataRepository = get<DefaultExtractorRepository>(),
            extractor = get()
        )
    }

    factory {
        RememberSearch(
            dispatcher = get(named(CoroutineModuleName.IO)),
            dao = get()
        )
    }

    factory {
        DefaultImageSearchByLabel(
            dispatcher = get(named(CoroutineModuleName.IO)),
            mediaImageRepository = get(),
            imageDataWithEmbeddingsDao = get(),
            rememberSearch = get()
        )
    } bind ImageSearchByLabel::class
}