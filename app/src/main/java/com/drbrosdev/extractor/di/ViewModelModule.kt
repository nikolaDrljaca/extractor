package com.drbrosdev.extractor.di

import com.drbrosdev.extractor.data.repository.DefaultExtractorDataRepository
import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.usecase.DefaultImageSearchByLabel
import com.drbrosdev.extractor.ui.components.extractorstatusbutton.ExtractorStatusButtonViewModel
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesViewModel
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsViewModel
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogViewModel
import com.drbrosdev.extractor.ui.image.ExtractorImageViewModel
import com.drbrosdev.extractor.ui.imageinfo.ExtractorImageInfoViewModel
import com.drbrosdev.extractor.ui.onboarding.worker.StartWorkerViewModel
import com.drbrosdev.extractor.ui.root.RootViewModel
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
        com.drbrosdev.extractor.ui.components.extractorsearchview.ExtractorSearchViewModel(
            savedStateHandle = get()
        )
    }

    viewModel {
        ExtractorStatusButtonViewModel(
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
        ExtractorImageViewModel(
            mediaImageRepository = get<DefaultMediaImageRepository>()
        )
    }

    viewModel {
        com.drbrosdev.extractor.ui.search.ExtractorSearchViewModel(
            query = it.get(),
            labelType = it.get(),
            imageSearch = get<DefaultImageSearchByLabel>(),
            stateHandle = get()
        )
    }

    viewModel {
        ExtractorImageInfoViewModel(
            mediaImageId = it.get(),
            extractorDataRepository = get<DefaultExtractorDataRepository>(),
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

    viewModel {
        ExtractorStatsViewModel(
            visualEmbedDao = get()
        )
    }
}
