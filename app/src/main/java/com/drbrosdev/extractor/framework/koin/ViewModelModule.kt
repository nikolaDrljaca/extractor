package com.drbrosdev.extractor.framework.koin

import com.drbrosdev.extractor.data.repository.DefaultExtractorRepository
import com.drbrosdev.extractor.domain.repository.DefaultMediaImageRepository
import com.drbrosdev.extractor.domain.usecase.image.search.DefaultImageSearchByLabel
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
            extractorDataRepository = get<DefaultExtractorRepository>(),
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
