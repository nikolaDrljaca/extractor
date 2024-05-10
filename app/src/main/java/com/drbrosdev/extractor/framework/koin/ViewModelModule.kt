package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.MainViewModel
import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.framework.mediastore.DefaultMediaStoreImageRepository
import com.drbrosdev.extractor.ui.albumviewer.ExtractorAlbumViewerViewModel
import com.drbrosdev.extractor.ui.allalbum.ExtractorAlbumsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.getmore.ExtractorGetMoreViewModel
import com.drbrosdev.extractor.ui.home.ExtractorHomeViewModel
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.imageviewer.ExtractorImageViewerModel
import com.drbrosdev.extractor.ui.onboarding.OnboardingViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import com.drbrosdev.extractor.ui.search.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.settings.ExtractorSettingsViewModel
import com.drbrosdev.extractor.ui.settings.bug.ExtractorFeedbackViewModel
import com.drbrosdev.extractor.ui.settings.periodic.ExtractorPeriodicWorkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        OnboardingViewModel(
            savedStateHandle = get(),
            completeOnboarding = get()
        )
    }

    viewModel {
        ExtractorGetMoreViewModel(
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
            spawnExtractorWork = get(),
            datastore = get(),
            stringProvider = get()
        )
    }

    viewModel {
        ExtractorImageInfoViewModel(
            mediaImageId = it.get(),
            extractorDataRepository = get<DefaultExtractorRepository>(),
        )
    }

    viewModel {
        ExtractorStatusDialogViewModel(
            spawnExtractorWork = get(),
            trackExtractionProgress = get()
        )
    }

    viewModel {
        ExtractorHomeViewModel(
            savedStateHandle = get(),
            compileVisualAlbum = get(),
            compileTextAlbum = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            homeScreenSettingsProvider = get(),
            extractionStatus = get()
        )
    }

    viewModel { params ->
        ExtractorAlbumViewerViewModel(
            stateHandle = get(),
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
        ExtractorAlbumsViewModel(
            savedStateHandle = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            stringProvider = get()
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
