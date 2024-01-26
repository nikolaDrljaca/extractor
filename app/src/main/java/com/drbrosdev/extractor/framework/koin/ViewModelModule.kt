package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.ui.album.ExtractorAlbumViewModel
import com.drbrosdev.extractor.ui.allalbum.ExtractorAlbumsViewModel
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.home.ExtractorHomeViewModel
import com.drbrosdev.extractor.ui.image.ExtractorImageViewModel
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import com.drbrosdev.extractor.ui.search.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.settings.ExtractorSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        StartWorkerViewModel(
            spawnExtractorWork = get(),
            datastore = get(),
        )
    }

    viewModel {
        RootViewModel(
            datastore = get()
        )
    }


    viewModel {
        ExtractorImageViewModel(
            loadMediaImageInfo = get()
        )
    }

    viewModel {
        ExtractorSearchViewModel(
            query = it.get(),
            keywordType = it.get(),
            imageSearch = get(),
            stateHandle = get(),
            extractionProgress = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            generateSuggestedKeywords = get(),
            spawnExtractorWork = get(),
            datastore = get()
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
            extractionProgress = get()
        )
    }

    viewModel {
        ExtractorStatsViewModel(
            visualEmbedDao = get()
        )
    }

    viewModel {
        ExtractorHomeViewModel(
            savedStateHandle = get(),
            compileVisualAlbum = get(),
            compileTextAlbum = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            homeScreenSettingsProvider = get()
        )
    }

    viewModel { params ->
        ExtractorAlbumViewModel(
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
}
