package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.ExtractionProgress
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.LoadMediaImageInfo
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.domain.usecase.ValidateToken
import com.drbrosdev.extractor.domain.usecase.extractor.DefaultExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.Extractor
import com.drbrosdev.extractor.domain.usecase.extractor.bulk.BulkExtractor
import com.drbrosdev.extractor.domain.usecase.image.create.DefaultInputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.create.InputImageFactory
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultImageSearchByKeyword
import com.drbrosdev.extractor.domain.usecase.image.search.ImageSearchByKeyword
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
        DefaultImageSearchByKeyword(
            dispatcher = get(named(CoroutineModuleName.IO)),
            imageEmbedDao = get(),
        )
    } bind ImageSearchByKeyword::class

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
        TokenizeText(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    }

    factory {
        ValidateToken(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    }

    factory {
        GenerateSuggestedKeywords(
            dispatcher = get(named(CoroutineModuleName.Default)),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            tokenizeText = get(),
            validateToken = get()
        )
    }

    factory {
        CompileTextAlbums(
            dispatcher = get(named(CoroutineModuleName.Default)),
            textEmbeddingDao = get(),
            imageEmbeddingsDao = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            tokenizeText = get(),
            validateToken = get()
        )
    }

    factory {
        SpawnExtractorWork(
            workManager = get()
        )
    }
}