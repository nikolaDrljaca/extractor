package com.drbrosdev.extractor.ui.overview

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesEvents
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesState
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchState
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CollectFlow
import com.drbrosdev.extractor.util.ScreenPreview
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
            onSearchClick = viewModel::onSearchClick,
            onMultiselectAction = viewModel.recommendedSearchesComponent::multiselectBarEventHandler,
            snackbarState = viewModel.snackbarHostState,
            statusPillState = statusPillState,
            recommendedSearchesState = collageRecommendationState,
            suggestedSearchState = suggestedSearchUiModel,
            overviewGridState = viewModel.recommendedSearchesComponent.overviewGridState
        )
    }
}

@ScreenPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorOverviewScreen(
                onHomeClick = {},
                onHubClick = {},
                onSearchClick = {},
                onMultiselectAction = {},
                overviewGridState = OverviewGridState(),
                statusPillState = ExtractorStatusPillState.OutOfSync,
                recommendedSearchesState = RecommendedSearchesState.Loading,
                suggestedSearchState = SuggestedSearchState.Loading,
                snackbarState = SnackbarHostState()
            )
        }
    }
}
