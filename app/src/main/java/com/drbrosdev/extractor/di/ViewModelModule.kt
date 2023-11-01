package com.drbrosdev.extractor.di

import com.drbrosdev.extractor.data.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.usecase.DefaultImageSearchByLabel
import com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.topbar.ExtractorTopBarViewModel
import com.drbrosdev.extractor.ui.extractorimageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.image.ImageDetailViewModel
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerViewModel
import com.drbrosdev.extractor.ui.result.SearchResultViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
import com.drbrosdev.extractor.ui.status.ExtractorStatusDialogViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
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
        ExtractorSearchViewModel()
    }

    viewModel {
        ExtractorTopBarViewModel(
            mediaImageRepository = get<DefaultMediaImageRepository>(),
            extractionEntityDao = get(),
            workManager = get()
        )
    }

    viewModel {
        PreviousSearchesViewModel(
            previousSearchDao = get()
        )
    }

    viewModel {
        ImageDetailViewModel(
            mediaImageRepository = get<DefaultMediaImageRepository>()
        )
    }

    viewModel {
        SearchResultViewModel(
            imageSearch = get<DefaultImageSearchByLabel>()
        )
    }

    viewModel {
        ExtractorImageInfoViewModel(
            mediaImageId = it.get(),
            extractorRepository = get<DefaultExtractorRepository>(),
            mediaImageRepository = get<DefaultMediaImageRepository>()
        )
    }

    viewModel {
        ExtractorStatusDialogViewModel(
            mediaImageRepository = get<DefaultMediaImageRepository>(),
            extractionEntityDao = get(),
            bulkExtractor = get(),
            workManager = get()
        )
    }
}
