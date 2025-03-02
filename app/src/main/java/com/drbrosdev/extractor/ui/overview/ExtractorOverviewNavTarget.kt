package com.drbrosdev.extractor.ui.overview

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
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
            context.launchShareIntent(it.uris)
        }

        ExtractorOverviewScreen(
            onHomeClick = viewModel::onHomeClick,
            onHubClick = viewModel::onHubClick,
            onMultiselectAction = viewModel.recommendedSearchesComponent::multiselectBarEventHandler,
            statusPillState = statusPillState,
            collageRecommendationState = collageRecommendationState,
            suggestedSearchUiModel = suggestedSearchUiModel,
            overviewState = viewModel.recommendedSearchesComponent.overviewGridState
        )
    }
}