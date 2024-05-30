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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    onHideBanner: () -> Unit,
    showBanner: Boolean,
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
                verticalArrangement = Arrangement.spacedBy(24.dp),
                state = lazyListState,
                contentPadding = systemBarsPadding
            ) {
                item {
                    Spacer(modifier = Modifier.height(44.dp))
                }

                item {
                    AnimatedVisibility(
                        visible = showBanner,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        AttentionContainer(
                            header = stringResource(R.string.what_are_these),
                            actionRow = {
                                ExtractorTextButton(onClick = onHideBanner) {
                                    Text(text = stringResource(id = R.string.got_it))
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.your_keywords_expl))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = stringResource(R.string.your_keywords_expl_2))
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
