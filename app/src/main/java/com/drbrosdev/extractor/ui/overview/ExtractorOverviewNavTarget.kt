package com.drbrosdev.extractor.ui.overview

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesEvents
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.launchShareIntent
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data object ExtractorOverviewNavTarget : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorOverviewViewModel = koinViewModel(
            viewModelStoreOwner = LocalActivity.current as ComponentActivity,
            parameters = { parametersOf(navigators) }
        )

        val statusPillState by viewModel.statusPillComponent.state
            .collectAsStateWithLifecycle()
        val collageRecommendationState by viewModel.recommendedSearchesComponent.state
            .collectAsStateWithLifecycle()
        val suggestedSearchUiModel by viewModel.suggestedSearchComponent.state
            .collectAsStateWithLifecycle()

        val context = LocalContext.current

        CollectFlow(viewModel.recommendedSearchesComponent.events) {
            when (it) {
                RecommendedSearchesEvents.AlbumCreated -> {
                    viewModel.showAlbumCreatedSnack(
                        message = context.getString(R.string.album_created),
                        actionLabel = context.getString(R.string.snack_view),
                    )
                }

                is RecommendedSearchesEvents.ShareImages ->
                    context.launchShareIntent(it.uris)
            }
        }

        ExtractorOverviewScreen(
            onHomeClick = viewModel::onHomeClick,
            onHubClick = viewModel::onHubClick,
            onMultiselectAction = viewModel.recommendedSearchesComponent::multiselectBarEventHandler,
            snackbarState = viewModel.snackbarHostState,
            statusPillState = statusPillState,
            collageRecommendationState = collageRecommendationState,
            suggestedSearchUiModel = suggestedSearchUiModel,
            overviewState = viewModel.recommendedSearchesComponent.overviewGridState
        )
    }
}