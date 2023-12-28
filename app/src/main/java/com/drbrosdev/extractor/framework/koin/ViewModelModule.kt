package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.domain.repository.DefaultAlbumRepository
import com.drbrosdev.extractor.domain.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.ui.album.ExtractorAlbumViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.home.ExtractorHomeViewModel
import com.drbrosdev.extractor.ui.image.ExtractorImageViewModel
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        StartWorkerViewModel(
            workManager = get(),
            datastore = get(),
        )
    }

    viewModel {
        RootViewModel(
            datastore = get()
        )
    }

    viewModel {
        PreviousSearchesViewModel(
            previousSearchDao = get()
        )
    }

    viewModel {
        ExtractorImageViewModel(
            loadMediaImageInfo = get()
        )
    }

    viewModel {
        com.drbrosdev.extractor.ui.search.ExtractorSearchViewModel(
            query = it.get(),
            labelType = it.get(),
            imageSearch = get(),
            stateHandle = get(),
            extractionProgress = get(),
            albumRepository = get<DefaultAlbumRepository>()
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
            bulkExtractor = get(),
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
            albumRepository = get<DefaultAlbumRepository>()
        )
    }

    viewModel { params ->
        ExtractorAlbumViewModel(
            stateHandle = get(),
            albumRepository = get<DefaultAlbumRepository>(),
            albumId = params.get()
        )
    }
}
