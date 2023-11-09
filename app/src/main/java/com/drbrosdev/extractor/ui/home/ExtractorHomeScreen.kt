package com.drbrosdev.extractor.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.usecase.LabelType
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchItemState
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearches
import com.drbrosdev.extractor.ui.components.previoussearch.PreviousSearchesEvents
import com.drbrosdev.extractor.ui.components.shared.ExtractorButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorHeader
import com.drbrosdev.extractor.ui.components.shared.ExtractorTopBar
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.components.stats.ExtractorStats
import com.drbrosdev.extractor.ui.components.stats.ExtractorStatsUiState

@Composable
fun ExtractorHomeScreen(
    statsUiState: ExtractorStatsUiState,
    previousSearches: List<PreviousSearchItemState>,
    onPreviousSearchEvents: (PreviousSearchesEvents) -> Unit,
    onStatClick: (String, LabelType) -> Unit,
    onSyncClick: () -> Unit,
) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(scrollState),
        constraintSet = homeScreenConstraintSet()
    ) {
        ExtractorStats(
            modifier = Modifier.layoutId(ViewIds.COMMON_V),
            state = statsUiState,
            onStatClick = onStatClick
        )

        ExtractorTopBar(
            modifier = Modifier.layoutId(ViewIds.TOP_BAR),
            leadingSlot = {
                ExtractorHeader(headerText = stringResource(id = R.string.extractor_home))
            },
            trailingSlot = {
                Spacer(modifier = Modifier.width(12.dp))
            }
        )

        PreviousSearches(
            modifier = Modifier.layoutId(ViewIds.PREV_SEARCH),
            onEvent = onPreviousSearchEvents,
            searches = previousSearches
        )

        OutlinedExtractorActionButton(
            onClick = onSyncClick,
            modifier = Modifier.layoutId(ViewIds.SYNC_BUTTON)
        ) {
            Icon(painter = painterResource(id = R.drawable.round_sync_24), contentDescription = "")
            Text(text = stringResource(R.string.sync_status))
        }

        ExtractorButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.layoutId(ViewIds.SETTINGS_BUTTON)
        ) {
            Icon(imageVector = Icons.Rounded.Settings, contentDescription = "")
            Text(text = stringResource(R.string.settings))
        }
    }
}

private fun homeScreenConstraintSet() = ConstraintSet {
    val previousSearch = createRefFor(ViewIds.PREV_SEARCH)
    val topBar = createRefFor(ViewIds.TOP_BAR)
    val common = createRefFor(ViewIds.COMMON_V)

    val settingsButton = createRefFor(ViewIds.SETTINGS_BUTTON)
    val syncButton = createRefFor(ViewIds.SYNC_BUTTON)
    val buttonGuideline = createGuidelineFromStart(0.5f)

    constrain(settingsButton) {
        start.linkTo(buttonGuideline, margin = 4.dp)
        top.linkTo(topBar.bottom, margin = 8.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(syncButton) {
        top.linkTo(topBar.bottom, margin = 8.dp)
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(buttonGuideline, margin = 4.dp)
        width = Dimension.fillToConstraints
    }

    constrain(previousSearch) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(common.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(topBar) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    }

    constrain(common) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(settingsButton.bottom, margin = 18.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val PREV_SEARCH = "prevSearch_1"
    const val TOP_BAR = "topBar"
    const val COMMON_V = "commonV"
    const val SYNC_BUTTON = "syncButton"
    const val SETTINGS_BUTTON = "settingsButton"
}

