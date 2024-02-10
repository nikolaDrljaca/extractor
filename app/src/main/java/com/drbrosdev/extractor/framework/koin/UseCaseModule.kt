package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TokenizeText
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.ValidateToken
import com.drbrosdev.extractor.domain.usecase.extractor.DefaultRunExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.RunBulkExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.RunExtractor
import com.drbrosdev.extractor.domain.usecase.image.create.CreateInputImage
import com.drbrosdev.extractor.domain.usecase.image.create.DefaultCreateInputImage
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultSearchImageByKeyword
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByKeyword
import com.drbrosdev.extractor.domain.usecase.label.extractor.ExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.label.extractor.MLKitExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.settings.ProvideHomeScreenSettings
import com.drbrosdev.extractor.domain.usecase.settings.ProvideMainActivitySettings
import com.drbrosdev.extractor.domain.usecase.text.extractor.ExtractTextEmbed
import com.drbrosdev.extractor.domain.usecase.text.extractor.MlKitExtractTextEmbed
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val useCaseModule = module {

    factory {
        DefaultCreateInputImage(context = androidContext())
    } bind CreateInputImage::class

    factory {
        MLKitExtractVisualEmbeds(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind ExtractVisualEmbeds::class

    factory {
        MlKitExtractTextEmbed(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind ExtractTextEmbed::class

    factory {
        DefaultRunExtractor(
            extractVisualEmbeds = get(),
            extractTextEmbed = get(),
            createInputImage = get(),
            dispatcher = get(named(CoroutineModuleName.Default)),
        )
    } bind RunExtractor::class

    factory {
        RunBulkExtractor(
            dispatcher = get(named(CoroutineModuleName.Default)),
            mediaImageRepository = get(),
            extractorRepository = get<DefaultExtractorRepository>(),
            runExtractor = get()
        )
    }

    factory {
        DefaultSearchImageByKeyword(
            dispatcher = get(named(CoroutineModuleName.IO)),
            imageEmbedDao = get(),
            tokenizeText = get()
        )
    } bind SearchImageByKeyword::class

    factory {
        TrackExtractionProgress(
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
            searchImageByKeyword = get<DefaultSearchImageByKeyword>(),
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
            searchImageByKeyword = get<DefaultSearchImageByKeyword>(),
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

    factory {
        ProvideHomeScreenSettings(
            dispatcher = get(named(CoroutineModuleName.Default)),
            settingsDatastore = get()
        )
    }

    factory {
        ProvideMainActivitySettings(
            dispatcher = get(named(CoroutineModuleName.Default)),
            settingsDatastore = get()
        )
    }
}