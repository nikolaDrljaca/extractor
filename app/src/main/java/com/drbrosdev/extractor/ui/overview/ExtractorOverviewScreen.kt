package com.drbrosdev.extractor.ui.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.toUri
import com.drbrosdev.extractor.ui.components.extractorimageitem.ExtractorImageItem
import com.drbrosdev.extractor.ui.components.recommendsearch.RecommendedSearchesState
import com.drbrosdev.extractor.ui.components.shared.ExtractorMultiselectActionBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchPill
import com.drbrosdev.extractor.ui.components.shared.ExtractorSnackbar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.MultiselectAction
import com.drbrosdev.extractor.ui.components.statuspill.ExtractorStatusPillState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearchState
import com.drbrosdev.extractor.ui.components.suggestsearch.SuggestedSearches
import com.drbrosdev.extractor.util.isScrollingUp
import com.drbrosdev.extractor.util.maxLineSpanItem

@Composable
fun ExtractorOverviewScreen(
    onHomeClick: () -> Unit,
    onHubClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMultiselectAction: (MultiselectAction) -> Unit,
    snackbarState: SnackbarHostState,
    overviewGridState: OverviewGridState,
    statusPillState: ExtractorStatusPillState,
    recommendedSearchesState: RecommendedSearchesState,
    suggestedSearchState: SuggestedSearchState
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
            state = overviewGridState.gridState.lazyGridState
        ) {
            maxLineSpanItem { Spacer(Modifier.padding(top = 54.dp)) }
            maxLineSpanItem { Spacer(Modifier.padding(top = 26.dp)) }

            item(span = { GridItemSpan(maxLineSpan) }, key = "search_pill") {
                Column {
                    ExtractorSearchPill(
                        onClick = onSearchClick,
                        // TODO: Recompositions?
                        enabled = suggestedSearchState !is SuggestedSearchState.Empty,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    SuggestedSearches(
                        suggestionUiModel = suggestedSearchState
                    )
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            when (recommendedSearchesState) {
                is RecommendedSearchesState.Content -> {
                    recommendedSearchesState.items.forEach {
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
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

                        itemsIndexed(
                            items = it.extractions,
                            key = { _, entry -> it.keyword + entry.mediaImageId.id }
                        ) { index, entry ->
                            ExtractorImageItem(
                                modifier = Modifier.animateItem(),
                                imageUri = entry.uri.toUri(),
                                size = 96,
                                onClick = {
                                    if (overviewGridState.onToggleCheckedItem(entry.mediaImageId)) {
                                        recommendedSearchesState.onImageClick(it.keyword, index)
                                    }
                                },
                                checkedState = overviewGridState.gridState[entry.mediaImageId],
                                onLongClick = {
                                    overviewGridState.onLongTap(entry.mediaImageId)
                                }
                            )
                        }
                    }
                }

                is RecommendedSearchesState.Empty -> maxLineSpanItem(key = "empty") {
                    Box(
                        modifier = Modifier.height(350.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                space = 12.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_hide_image_24),
                                contentDescription = "No result",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(64.dp)
                            )
                            Text(
                                text = "Could not generate recommendations.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.width(IntrinsicSize.Max),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                is RecommendedSearchesState.Loading -> maxLineSpanItem(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            trackColor = Color.Transparent
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(36.dp)) }
        }

        AnimatedVisibility(
            modifier = Modifier
                .layoutId(ViewIds.TOP_BAR),
            visible = overviewGridState.gridState.lazyGridState.isScrollingUp(),
            enter = slideInVertically(initialOffsetY = { -(1.5 * it).toInt() }),
            exit = slideOutVertically(targetOffsetY = { -(1.5 * it).toInt() })
        ) {
            ExtractorTopBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                onHubClick = onHubClick,
                onHomeClick = onHomeClick,
                statusPillState = statusPillState
            )
        }

        AnimatedVisibility(
            visible = overviewGridState.showSearchFab,
            modifier = Modifier
                .layoutId(ViewIds.FAB),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                onClick = onSearchClick,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search"
                )
            }
        }

        AnimatedVisibility(
            visible = overviewGridState.multiselectState,
            modifier = Modifier
                .layoutId(ViewIds.ACTION_BAR),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ExtractorMultiselectActionBar(
                onAction = onMultiselectAction
            )
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier
                .layoutId(ViewIds.SNACKBAR)
        ) {
            ExtractorSnackbar(it)
        }
    }
}

private fun overviewScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val mainContent = createRefFor(ViewIds.MAIN_CONTENT)
    val fab = createRefFor(ViewIds.FAB)
    val actionBar = createRefFor(ViewIds.ACTION_BAR)
    val snackbar = createRefFor(ViewIds.SNACKBAR)

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

    constrain(snackbar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        bottom.linkTo(fab.top, margin = 8.dp)
    }

    constrain(actionBar) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        bottom.linkTo(parent.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val SNACKBAR = "snackbar_view"
    const val FAB = "fab_view"
    const val MAIN_CONTENT = "main_content_view"
    const val TOP_BAR = "top_bar_view"
    const val ACTION_BAR = "action_bar_view"
}

