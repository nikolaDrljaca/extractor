package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.MainViewModel
import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultLupaImageRepository
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.framework.workmanager.DefaultExtractorWorkerService
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.dialog.userembed.ExtractorUserEmbedViewModel
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerModel
import com.drbrosdev.extractor.ui.onboarding.OnboardingViewModel
import com.drbrosdev.extractor.ui.overview.ExtractorOverviewViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import com.drbrosdev.extractor.ui.search.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.settings.ExtractorSettingsViewModel
import com.drbrosdev.extractor.ui.settings.bug.ExtractorFeedbackViewModel
import com.drbrosdev.extractor.ui.settings.clearevent.ExtractorClearEventsViewModel
import com.drbrosdev.extractor.ui.settings.index.ExtractorResetIndexViewModel
import com.drbrosdev.extractor.ui.settings.periodic.ExtractorPeriodicWorkViewModel
import com.drbrosdev.extractor.ui.shop.ExtractorHubViewModel
import com.drbrosdev.extractor.ui.usercollage.ExtractorUserCollageViewModel
import com.drbrosdev.extractor.ui.yourspace.ExtractorYourSpaceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        ExtractorOverviewViewModel(
            trackExtractionProgress = get(),
            compileSearchSuggestions = get(),
            generateMostCommonExtractionBundles = get(),
            dataStore = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            lupaImageRepository = get<DefaultLupaImageRepository>(),
            navigators = it.get()
        )
    }

    viewModel {
        ExtractorResetIndexViewModel(
            lupaImageRepository = get<DefaultLupaImageRepository>(),
            workerService = get<DefaultExtractorWorkerService>(),
            extractionProgress = get()
        )
    }

    viewModel {
        ExtractorClearEventsViewModel(
            eventDao = get()
        )
    }

    viewModel {
        OnboardingViewModel(
            savedStateHandle = get(),
            completeOnboarding = get()
        )
    }

    viewModel {
        ExtractorHubViewModel(
            datastore = get()
        )
    }

    viewModel {
        RootViewModel(
            datastore = get(),
            permissionService = get()
        )
    }

    viewModel {
        ExtractorImageViewerModel(
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>(),
            lupaImageRepository = get<DefaultLupaImageRepository>(),
            images = it.get(),
            initialIndex = it.get()
        )
    }

    viewModel {
        ExtractorSearchViewModel(
            imageSearch = get(),
            stateHandle = get(),
            trackExtractionProgress = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            generateSuggestedKeywords = get(),
            datastore = get(),
            searchCountPositiveDelta = get(),
            stringProvider = get(),
            navigators = it.get()
        )
    }

    viewModel {
        ExtractorImageInfoViewModel(
            mediaImageId = it.get(),
            stateHandle = get(),
            extractorDataRepository = get<DefaultLupaImageRepository>(),
        )
    }

    viewModel {
        ExtractorStatusDialogViewModel(
            workerService = get<DefaultExtractorWorkerService>(),
            trackExtractionProgress = get()
        )
    }

    viewModel { params ->
        ExtractorYourSpaceViewModel(
            savedStateHandle = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            generateUserCollage = get(),
            navigators = params.get()
        )
    }

    viewModel { params ->
        ExtractorAlbumViewerViewModel(
            stateHandle = get(),
            workerService = get<DefaultExtractorWorkerService>(),
            albumRepository = get<DefaultAlbumRepository>(),
            albumId = params.get(),
            navigators = params.get()
        )
    }

    viewModel {
        ExtractorSettingsViewModel(
            settingsDatastore = get()
        )
    }

    viewModel {
        ExtractorUserEmbedViewModel(
            mediaImageId = it.get(),
            stateHandle = get(),
            suggestUserKeywords = get(),
            lupaImageRepository = get<DefaultLupaImageRepository>()
        )
    }

    viewModel {
        ExtractorUserCollageViewModel(
            stateHandle = get(),
            generateUserCollage = get(),
            datastore = get()
        )
    }

    viewModel {
        MainViewModel(
            mainSettings = get()
        )
    }

    viewModel {
        ExtractorPeriodicWorkViewModel(
            workManager = get()
        )
    }

    viewModel {
        ExtractorFeedbackViewModel(
            savedStateHandle = get(),
            generateEmailContent = get()
        )
    }
}
