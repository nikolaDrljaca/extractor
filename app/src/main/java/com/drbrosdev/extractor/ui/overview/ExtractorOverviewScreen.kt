package com.drbrosdev.extractor.ui.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.domain.model.Extraction
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchPill
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchUiModel
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearches
import com.drbrosdev.extractor.ui.components.usercollage.CollageRecommendationState
import com.drbrosdev.extractor.ui.components.usercollage.ExtractionCollage
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview
import com.drbrosdev.extractor.util.isScrollingUp

@Composable
fun ExtractorOverviewScreen(
    onHomeClick: () -> Unit,
    onHubClick: () -> Unit,
    onCollageItemClicked: (entry: Extraction, collage: ExtractionCollage) -> Unit,
    overviewState: OverviewGridState,
    statusPillState: ExtractorStatusPillState,
    collageRecommendationState: CollageRecommendationState,
    suggestedSearchUiModel: SuggestedSearchUiModel
) {
    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        constraintSet = overviewScreenConstraintSet()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .layoutId(ViewIds.MAIN_CONTENT),
            columns = GridCells.Adaptive(minSize = 96.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            state = overviewState.gridState.lazyGridState
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.padding(top = 54.dp)) }
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.padding(top = 54.dp)) }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    ExtractorSearchPill(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    SuggestedSearches(
                        suggestionUiModel = suggestedSearchUiModel
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            when (collageRecommendationState) {
                is CollageRecommendationState.Content -> {
                    collageRecommendationState.items.forEach {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "# ${it.keyword}",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    bottom = 4.dp,
                                    top = 12.dp
                                )
                            )
                        }

                        items(it.extractions) { entry ->
                            ExtractorImageItem(
                                modifier = Modifier.animateItem(),
                                imageUri = entry.uri.toUri(),
                                size = 96,
                                onClick = {
                                    if (overviewState.onToggleCheckedItem(entry.mediaImageId)) {
                                        onCollageItemClicked(entry, it)
                                    }
                                },
                                checkedState = overviewState.gridState[entry.mediaImageId],
                                onLongClick = {
                                    overviewState.onLongTap(entry.mediaImageId)
                                }
                            )
                        }
                    }
                }

                // TODO
                is CollageRecommendationState.Empty -> item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recommendations found")
                    }
                }

                // TODO
                is CollageRecommendationState.Loading -> item {
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

        AnimatedVisibility(
            modifier = Modifier
                .layoutId(ViewIds.TOP_BAR),
            visible = overviewState.gridState.lazyGridState.isScrollingUp(),
            enter = slideInVertically(initialOffsetY = { -(1.3 * it).toInt() }),
            exit = slideOutVertically(targetOffsetY = { -(1.3 * it).toInt() })
        ) {
            ExtractorTopBar(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                onHubClick = onHubClick,
                onHomeClick = onHomeClick,
                statusPillState = statusPillState
            )
        }

        AnimatedVisibility(
            visible = overviewState.showSearchFab,
            modifier = Modifier
                .layoutId(ViewIds.FAB),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search"
                )
            }
        }

        AnimatedVisibility(
            visible = overviewState.multiselectState,
            modifier = Modifier
                .layoutId(ViewIds.ACTION_BAR),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorMultiselectActionBar(
                onAction = {}
            )
        }
    }
}

private fun overviewScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)
    val fab = createRefFor(ViewIds.FAB)
    val actionBar = createRefFor(ViewIds.ACTION_BAR)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
        height = Dimension.wrapContent
    }

    constrain(mainContent) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
    }

    constrain(fab) {
        end.linkTo(parent.end, margin = 32.dp)
        bottom.linkTo(parent.bottom, margin = 32.dp)
    }

    constrain(actionBar) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        bottom.linkTo(parent.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val FAB = "fab_view"
    const val MAIN_CONTENT = "main_content_view"
    const val TOP_BAR = "top_bar_view"
    const val ACTION_BAR = "action_bar_view"
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExtractorOverviewScreen(
                onHomeClick = {},
                onHubClick = {},
                onCollageItemClicked = { _, _ -> },
                overviewState = OverviewGridState(),
                statusPillState = ExtractorStatusPillState.OutOfSync,
                collageRecommendationState = CollageRecommendationState.Loading,
                suggestedSearchUiModel = SuggestedSearchUiModel.Loading
            )
        }
    }
}
