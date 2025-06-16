package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.usecase.CompleteOnboarding
import com.drbrosdev.extractor.domain.usecase.GenerateFeedbackEmailContent
import com.drbrosdev.extractor.domain.usecase.album.CleanupAlbum
import com.drbrosdev.extractor.domain.usecase.album.StoreAlbums
import com.drbrosdev.extractor.domain.usecase.extractor.BulkExtractLupaAnnotations
import com.drbrosdev.extractor.domain.usecase.extractor.StartExtraction
import com.drbrosdev.extractor.domain.usecase.extractor.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.generate.CompileMostCommonTextEmbeds
import com.drbrosdev.extractor.domain.usecase.generate.CompileMostCommonVisualEmbeds
import com.drbrosdev.extractor.domain.usecase.generate.GenerateMostCommonExtractionBundles
import com.drbrosdev.extractor.domain.usecase.generate.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.image.BuildFtsQuery
import com.drbrosdev.extractor.domain.usecase.image.SearchCountPositiveDelta
import com.drbrosdev.extractor.domain.usecase.image.SearchImageSideEffects
import com.drbrosdev.extractor.domain.usecase.image.SearchImages
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultSearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultSearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByDateRange
import com.drbrosdev.extractor.domain.usecase.image.search.SearchImageByQuery
import com.drbrosdev.extractor.domain.usecase.settings.ProvideMainActivitySettings
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.domain.usecase.suggestion.GenerateSuggestedKeywords
import com.drbrosdev.extractor.domain.usecase.suggestion.SuggestUserKeywords
import com.drbrosdev.extractor.domain.usecase.token.GenerateMostCommonTokens
import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.framework.PlayAppReviewService
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.framework.workmanager.DefaultExtractorWorkerService
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val useCaseModule = module {

    factory {
        GenerateMostCommonExtractionBundles(
            compileMostCommonVisualEmbeds = get(),
            compileMostCommonTextEmbeds = get()
        )
    }

    factory {
        SuggestUserKeywords(
            dispatcher = get(named(CoroutineModuleName.Default)),
            userEmbeddingDao = get(),
            tokenizeText = get(),
        )
    }

    factory {
        GenerateUserCollage(
            dispatcher = get(named(CoroutineModuleName.Default)),
            userEmbeddingDao = get(),
            userExtractionDao = get()
        )
    }

    factory {
        BulkExtractLupaAnnotations(
            mediaImageRepository = get(),
            extractorRepository = get<DefaultExtractorRepository>(),
            extractLupaAnnotations = get()
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
            buildFtsQuery = get(),
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>(),
            trackExtractionProgress = get()
        )
    } bind SearchImageByQuery::class

    single {
        TrackExtractionProgress(
            dispatcher = get(named(CoroutineModuleName.Default)),
            repo = get(),
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>(),
            workerService = get<DefaultExtractorWorkerService>()
        )
    }

    factory { GenerateMostCommonTokens() }

    factory {
        CompileMostCommonVisualEmbeds(
            repo = get(),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            tokenizeText = get(),
            generateMostCommonTokens = get(),
        )
    }

    factory {
        TokenizeText(
            dispatcher = get(named(CoroutineModuleName.Default))
        )
    }

    factory {
        GenerateSuggestedKeywords(
            extractionDao = get(),
            dataStore = get(),
            compileSearchSuggestions = get()
        )
    }

    factory {
        CompileSearchSuggestions(
            dispatcher = get(named(CoroutineModuleName.Default)),
            textEmbeddingDao = get(),
            userEmbeddingDao = get(),
            visualEmbeddingDao = get(),
            tokenizeText = get()
        )
    }

    factory {
        CompileMostCommonTextEmbeds(
            repo = get(),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            tokenizeText = get(),
            generateMostCommonTokens = get(),
        )
    }

    factory {
        StoreAlbums(
            albumRepository = get<DefaultAlbumRepository>()
        )
    }

    factory {
        CleanupAlbum(
            mediaStoreImageRepository = get(),
            albumRepository = get()
        )
    }

    factory { params ->
        StartExtraction(
            mediaImageRepository = get(),
            extractionRepository = get(),
            inferenceService = params.get()
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
            workerService = get()
        )
    }

    factory {
        SearchImages(
            dispatcher = get(named(CoroutineModuleName.Default)),
            searchImageByQuery = get<DefaultSearchImageByQuery>(),
            searchImageByDateRange = get<DefaultSearchImageByDateRange>(),
            sideEffects = get(),
            dataStore = get(),
        )
    }

    factory {
        SearchImageSideEffects(
            dispatcher = get(named(CoroutineModuleName.Default)),
            datastore = get(),
            appReviewService = get<PlayAppReviewService>()
        )
    }

    factory {
        DefaultSearchImageByDateRange(
            dispatcher = get(named(CoroutineModuleName.Default)),
            extractionDao = get()
        )
    } bind SearchImageByDateRange::class
}