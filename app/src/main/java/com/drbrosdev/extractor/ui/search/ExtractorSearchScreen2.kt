package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.domain.usecase.GenerateUserCollage
import com.drbrosdev.extractor.domain.usecase.TrackExtractionProgress
import com.drbrosdev.extractor.domain.usecase.album.CompileTextAlbums
import com.drbrosdev.extractor.domain.usecase.suggestion.CompileSearchSuggestions
import com.drbrosdev.extractor.framework.navigation.NavTarget
import com.drbrosdev.extractor.framework.navigation.Navigators
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchPill
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.statuspill.StatusPillComponent
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchComponent
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchUiModel
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearches
import com.drbrosdev.extractor.ui.components.usercollage.CollageRecommendationState
import com.drbrosdev.extractor.ui.components.usercollage.CollageRecommendationsComponent
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorCollageItem
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExtractorSearchScreen2(
    modifier: Modifier = Modifier,
    statusPillState: ExtractorStatusPillState,
    collageRecommendationState: CollageRecommendationState,
    suggestedSearchUiModel: SuggestedSearchUiModel
) {
    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = searchScreenConstraintSet()
    ) {
        // scrollable content
        LazyColumn(
            modifier = Modifier
                .layoutId(ViewIds2.MAIN_CONTENT),
        ) {
            item { Spacer(Modifier.height(72.dp)) }
            // search bar -- total rework
            item {
                ExtractorSearchPill(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            // suggest searches using keywords/algorithm
            // that is used when generating albums based on most common keywords
            // clicking these navigates to search and uses this to search
            item {
                SuggestedSearches(
                    suggestionUiModel = suggestedSearchUiModel
                )
            }
            item { Spacer(Modifier.height(12.dp)) }
            // suggestion-generated view of images similar to collage
            // these are searched with the current chatgpt like search
            // instead of displaying search suggestions - just search and display
            when (collageRecommendationState) {
                is CollageRecommendationState.Content ->
                    items(collageRecommendationState.items) {
                        ExtractorCollageItem(
                            modifier = Modifier.padding(top = 12.dp),
                            onItemClick = {},
                            keyword = it.keyword,
                            extractions = it.extractions
                        )
                    }

                // TODO
                CollageRecommendationState.Empty -> item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recommendations found")
                    }
                }

                // TODO
                CollageRecommendationState.Loading -> item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading")
                    }
                }
            }
            item { Spacer(Modifier.height(36.dp)) }
        }

        // TODO hide when scrolling down - translate on y axis animation
        ExtractorTopBar(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .layoutId(ViewIds2.TOP_BAR),
            onHomeClick = {},
            onAlbumsClick = {},
            statusPillState = statusPillState
        )

        // TODO on scroll down hide
        FloatingActionButton(
            modifier = Modifier
                .layoutId(ViewIds2.FAB),
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search"
            )
        }
    }
}

class ExtractorSearchViewModel2(
    private val trackExtractionProgress: TrackExtractionProgress,
    private val compileSearchSuggestions: CompileSearchSuggestions,
    private val compileTextAlbums: CompileTextAlbums,
    private val dataStore: ExtractorDataStore,
    private val generateUserCollage: GenerateUserCollage,
) : ViewModel() {
    val statusPillComponent = StatusPillComponent(
        coroutineScope = viewModelScope,
        trackProgress = trackExtractionProgress,
        dataStore = dataStore
    )

    val suggestedSearchComponent = SuggestedSearchComponent(
        coroutineScope = viewModelScope,
        compileSearchSuggestions = compileSearchSuggestions,
        onSearch = {}
    )

    val collageRecommendationComponent = CollageRecommendationsComponent(
        coroutineScope = viewModelScope,
        generateUserCollage = generateUserCollage,
        compileTextAlbums = compileTextAlbums
    )
}

@Parcelize
data object ExtractorSearchNavTarget2 : NavTarget {

    @Composable
    override fun Content(navigators: Navigators) {
        val viewModel: ExtractorSearchViewModel2 = koinViewModel()

        val statusPillState by viewModel.statusPillComponent.state
            .collectAsStateWithLifecycle()
        val collageRecommendationState by viewModel.collageRecommendationComponent.state
            .collectAsStateWithLifecycle()
        val suggestedSearchUiModel by viewModel.suggestedSearchComponent.state
            .collectAsStateWithLifecycle()

        ExtractorSearchScreen2(
            statusPillState = statusPillState,
            collageRecommendationState = collageRecommendationState,
            suggestedSearchUiModel = suggestedSearchUiModel
        )
    }
}


private fun searchScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds2.TOP_BAR)
    val mainContent = createRefFor(ViewIds2.MAIN_CONTENT)
    val fab = createRefFor(ViewIds2.FAB)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top, margin = 24.dp)
        width = Dimension.fillToConstraints
        height = Dimension.wrapContent
    }

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom)
    }

    constrain(fab) {
        end.linkTo(parent.end, margin = 32.dp)
        bottom.linkTo(parent.bottom, margin = 32.dp)
    }
}

private object ViewIds2 {
    const val FAB = "fab_view"
    const val MAIN_CONTENT = "main_content_view"
    const val TOP_BAR = "top_bar_view"
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorSearchScreen2(
                statusPillState = ExtractorStatusPillState.OutOfSync,
                collageRecommendationState = CollageRecommendationState.Loading,
                suggestedSearchUiModel = SuggestedSearchUiModel.Loading
            )
        }
    }
}
