package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.BuildUserCollage
import com.drbrosdev.extractor.domain.usecase.CompleteOnboarding
import com.drbrosdev.extractor.domain.usecase.GenerateFeedbackEmailContent
import com.drbrosdev.extractor.domain.usecase.SpawnAlbumCleanupWork
import com.drbrosdev.extractor.domain.usecase.SpawnExtractorWork
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.BuildNewAlbumPayload
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.album.CompileVisualAlbum
import com.drbrosdev.extractor.domain.usecase.extractor.DefaultRunExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.RunBulkExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.RunExtractor
import com.drbrosdev.extractor.domain.usecase.extractor.text.ExtractTextEmbed
import com.drbrosdev.extractor.domain.usecase.extractor.text.MlKitExtractTextEmbed
import com.drbrosdev.extractor.domain.usecase.extractor.visual.ExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.extractor.visual.MLKitExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.extractor.visual.MediaPipeExtractVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.image.BuildFtsQuery
import com.drbrosdev.extractor.domain.usecase.image.SearchCountPositiveDelta
import com.drbrosdev.extractor.domain.usecase.image.SearchImages
import com.drbrosdev.extractor.domain.usecase.image.create.CreateInputImage
import com.drbrosdev.extractor.domain.usecase.image.create.DefaultCreateInputImage
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultSearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultSearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.settings.ProvideHomeScreenSettings
import com.drbrosdev.extractor.domain.usecase.settings.ProvideMainActivitySettings
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.suggestion.SuggestUserKeywords
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val useCaseModule = module {

    factory {
        SuggestUserKeywords(
            dispatcher = get(named(CoroutineModuleName.Default)),
            userEmbeddingDao = get(),
            tokenizeText = get(),
        )
    }

    factory {
        BuildUserCollage(
            dispatcher = get(named(CoroutineModuleName.Default)),
            userEmbeddingDao = get(),
            userExtractionDao = get()
        )
    }

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
            dispatcher = get(named(CoroutineModuleName.Default)),
            tokenizeText = get()
        )
    } bind ExtractTextEmbed::class

    factory {
        DefaultRunExtractor(
            createInputImage = get(),
            extractVisualEmbeds = get<MLKitExtractVisualEmbeds>(),
            extractTextEmbed = get(),
            mediaPipeExtractVisualEmbeds = get<MediaPipeExtractVisualEmbeds>(),
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
        SearchCountPositiveDelta(
            dataStore = get()
        )
    }

    factory {
        DefaultSearchImageByQuery(
            dispatcher = get(named(CoroutineModuleName.IO)),
            imageEmbedDao = get(),
            tokenizeText = get(),
            buildFtsQuery = get()
        )
    } bind SearchImageByQuery::class

    single {
        TrackExtractionProgress(
            dispatcher = get(named(CoroutineModuleName.Default)),
            repo = get(),
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>(),
            workManager = get()
        )
    }

    factory { GenerateMostCommonTokens() }

    factory { BuildNewAlbumPayload() }

    factory {
        CompileVisualAlbum(
            repo = get(),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            albumRepository = get<DefaultAlbumRepository>(),
            tokenizeText = get(),
            generateMostCommonTokens = get(),
            buildNewAlbumPayload = get()
        )
    }

    factory {
        TokenizeText(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    }

    factory {
        GenerateSuggestedKeywords(
            dispatcher = get(named(CoroutineModuleName.Default)),
            visualEmbeddingDao = get(),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            extractionDao = get(),
            tokenizeText = get(),
            dataStore = get()
        )
    }

    factory {
        CompileTextAlbums(
            repo = get(),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            albumRepository = get<DefaultAlbumRepository>(),
            tokenizeText = get(),
            generateMostCommonTokens = get(),
            buildNewAlbumPayload = get()
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

    factory {
        BuildFtsQuery()
    }

    factory {
        GenerateFeedbackEmailContent(
            dispatcher = get(named(CoroutineModuleName.Default)),
            eventLogDao = get()
        )
    }

    factory {
        CompleteOnboarding(
            dispatcher = get(named(CoroutineModuleName.Default)),
            dataStore = get(),
            spawnExtractorWork = get()
        )
    }

    factory {
        SearchImages(
            dispatcher = get(named(CoroutineModuleName.Default)),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            searchImageByDateRange = get<DefaultSearchImageByDateRange>(),
            dataStore = get(),
        )
    }

    factory {
        DefaultSearchImageByDateRange(
            dispatcher = get(named(CoroutineModuleName.Default)),
            extractionDao = get()
        )
    } bind SearchImageByDateRange::class

    factory {
        SpawnAlbumCleanupWork(
            workManager = get()
        )
    }

    // Keep this single due to the ImageClassifier instance created inside
    // NOTE: Maybe move this into a provider use case?
    single {
        MediaPipeExtractVisualEmbeds(
            context = androidContext(),
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    } bind ExtractVisualEmbeds::class
}