package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.ExtractionProgress
import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.LoadMediaImageInfo
import com.drbrosdev.extractor.domain.usecase.RememberSearch
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.extractor.DefaultExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor
import com.drbrosdev.extractor.domain.usecase.image.create.DefaultInputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.image.search.ImageSearchByLabel
import com.drbrosdev.extractor.domain.usecase.label.extractor.MLKitVisualEmbedExtractor
import com.drbrosdev.extractor.domain.usecase.label.extractor.VisualEmbedExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.MlKitTextEmbedExtractor
import com.drbrosdev.extractor.domain.usecase.text.extractor.TextEmbedExtractor
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val useCaseModule = module {

    factory {
        DefaultInputImageFactory(context = androidContext())
    } bind InputImageFactory::class

    factory {
        MLKitVisualEmbedExtractor(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind VisualEmbedExtractor::class

    factory {
        MlKitTextEmbedExtractor(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind TextEmbedExtractor::class

    factory {
        DefaultExtractor(
            visualEmbedExtractor = get(),
            textEmbedExtractor = get(),
            inputImageFactory = get(),
            dispatcher = get(named(CoroutineModuleName.Default)),
        )
    } bind Extractor::class

    factory {
        BulkExtractor(
            dispatcher = get(named(CoroutineModuleName.Default)),
            mediaImageRepository = get(),
            extractorRepository = get<DefaultExtractorRepository>(),
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
            imageEmbedDao = get(),
            rememberSearch = get()
        )
    } bind ImageSearchByLabel::class

    factory {
        LoadMediaImageInfo(
            dispatcher = get(named(CoroutineModuleName.Default)),
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>()
        )
    }

    factory {
        ExtractionProgress(
            dispatcher = get(named(CoroutineModuleName.Default)),
            extractionDao = get(),
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>(),
            workManager = get()
        )
    }

    factory {
        CompileVisualAlbum(
            dispatcher = get(named(CoroutineModuleName.Default)),
            visualEmbeddingDao = get(),
            imageEmbeddingsDao = get(),
            albumRepository = get<DefaultAlbumRepository>()
        )
    }

    factory {
        CompileTextAlbums(
            dispatcher = get(named(CoroutineModuleName.Default)),
            textEmbeddingDao = get(),
            imageEmbeddingsDao = get(),
            albumRepository = get<DefaultAlbumRepository>()
        )
    }

    factory {
        SpawnExtractorWork(
            workManager = get()
        )
    }
}