package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.MainViewModel
import com.drbrosdev.extractor.data.album.DefaultAlbumRepository
import com.drbrosdev.extractor.data.extraction.DefaultExtractorRepository
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.framework.workmanager.DefaultExtractorWorkerService
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.dialog.userembed.ExtractorUserEmbedViewModel
import com.drbrosdev.extractor.ui.home.ExtractorHomeViewModel
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
import com.drbrosdev.extractor.ui.shop.ExtractorShopSearchViewModel
import com.drbrosdev.extractor.ui.usercollage.ExtractorUserCollageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        ExtractorOverviewViewModel(
            trackExtractionProgress = get(),
            compileSearchSuggestions = get(),
            compileTextAlbums = get(),
            dataStore = get(),
            generateUserCollage = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            navigators = it.get()
        )
    }

    viewModel {
        ExtractorResetIndexViewModel(
            extractorRepository = get<DefaultExtractorRepository>(),
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
        ExtractorShopSearchViewModel(
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
            mediaStoreImageRepository = get<DefaultMediaStoreImageRepository>()
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
            extractorDataRepository = get<DefaultExtractorRepository>(),
        )
    }

    viewModel {
        ExtractorStatusDialogViewModel(
            workerService = get<DefaultExtractorWorkerService>(),
            trackExtractionProgress = get()
        )
    }

    viewModel {
        ExtractorHomeViewModel(
            savedStateHandle = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            generateUserCollage = get(),
        )
    }

    viewModel { params ->
        ExtractorAlbumViewerViewModel(
            stateHandle = get(),
            workerService = get<DefaultExtractorWorkerService>(),
            albumRepository = get<DefaultAlbumRepository>(),
            albumId = params.get()
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
            extractorRepository = get<DefaultExtractorRepository>()
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
