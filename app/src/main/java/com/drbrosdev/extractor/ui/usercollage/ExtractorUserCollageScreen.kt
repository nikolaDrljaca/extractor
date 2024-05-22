package com.drbrosdev.extractor.ui.usercollage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.AttentionContainer
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTextButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBarState
import com.drbrosdev.extractor.ui.components.usercollage.ExtractorUserCollageItem

@Composable
fun ExtractorUserCollageScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    state: ExtractorUserCollageUiState
) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val lazyListState = rememberLazyListState()
    val extractorTopBarState = remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) ExtractorTopBarState.ELEVATED
            else ExtractorTopBarState.NORMAL
        }
    }

    var containerVisible by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        when (state) {
            ExtractorUserCollageUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round
                )
            }

            is ExtractorUserCollageUiState.Content -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                state = lazyListState,
                contentPadding = systemBarsPadding
            ) {
                item {
                    Spacer(modifier = Modifier.height(56.dp))
                }

                item {
                    AnimatedVisibility(
                        visible = containerVisible,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AttentionContainer(
                            header = "What are these?",
                            actionRow = {
                                ExtractorTextButton(onClick = {
                                    containerVisible = false
                                }) {
                                    Text(text = "Got It!")
                                }
                            }
                        ) {
                            Text(text = "A collection of all images where you have left your own tag! They are grouped by keyword for organisation.")
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "This collection will constantly update as you keep adding you own tags. You can use this area to tag images you want to find quickly.")
                        }
                    }
                }

                items(state.collages) { collage ->
                    ExtractorUserCollageItem(
                        onItemClick = { state.onItemClicked(collage.userEmbed, it) },
                        onShareClick = { state.onShare(collage.userEmbed) },
                        keyword = collage.userEmbed,
                        extractions = collage.extractions
                    )
                }
            }
        }

        ExtractorTopBar(
            modifier = Modifier.align(Alignment.TopCenter),
            leadingSlot = {
                BackIconButton(onBack = onBack)
                ExtractorHeader(headerText = stringResource(id = R.string.your_keywords))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            },
            centerSlot = {},
            state = extractorTopBarState.value
        )
    }
}

private fun userCollageScreenConstraintSet() = ConstraintSet {
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val banner = createRefFor(ViewIds.BANNER)
    val collages = createRefFor(ViewIds.COLLAGES)

    constrain(topBar) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(parent.top)
        width = Dimension.fillToConstraints
    }

    constrain(banner) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        top.linkTo(topBar.bottom, margin = 8.dp)
        width = Dimension.fillToConstraints
    }

    constrain(collages) {

    }
}

private object ViewIds {
    const val TOP_BAR = "top_bar"
    const val BANNER = "banner"
    const val COLLAGES = "collages"
}
