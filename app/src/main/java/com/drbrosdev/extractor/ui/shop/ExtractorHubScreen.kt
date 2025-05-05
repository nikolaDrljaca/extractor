package com.drbrosdev.extractor.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.rewards.RewardSnackbar
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorSearchCountPill
import com.drbrosdev.extractor.ui.components.shared.ExtractorShopPlaceholder
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialog
import com.drbrosdev.extractor.ui.dialog.status.ExtractorStatusDialogUiState

@Composable
fun ExtractorHubScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onPurchaseItemClick: () -> Unit,
    snackbarState: SnackbarHostState,
    statusState: ExtractorStatusDialogUiState,
    searchCount: Int
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )
    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .verticalScroll(scrollState),
            constraintSet = shopScreenConstraintSet()
        ) {
            // header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId(ViewIds.HEADER),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackIconButton(
                    onBack = onBack,
                )
            }

            // extraction status
            Column(modifier = Modifier.layoutId(ViewIds.STATUS)) {
                ExtractorStatusDialog(
                    modifier = Modifier,
                    state = statusState
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // search count
            Column(modifier = Modifier.layoutId(ViewIds.SEARCHES)) {
                Text(
                    text = stringResource(R.string.num_of_searches),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExtractorSearchCountPill(
                    searchCount = searchCount,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // get more with purchase
            Column(
                modifier = Modifier.layoutId(ViewIds.BUY_VIEW),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.direct_support),
                    style = textStyle
                )

                Spacer(modifier = Modifier.height(4.dp))

                ExtractorShopPlaceholder(
                    modifier = Modifier.fillMaxWidth()
                )

//                FlowRow(
//                    verticalArrangement = Arrangement.spacedBy(4.dp),
//                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                    maxItemsInEachRow = 2
//                ) {
//                    repeat(4) {
//                        ExtractorShopItem(
//                            onClick = onPurchaseItemClick,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = stringResource(R.string.buy_disclaimer),
//                    style = smallLabel
//                )
            }

            // reset index to get more
            Column(
                modifier = Modifier.layoutId(ViewIds.ALTERNATIVE),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.alternative),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.alternative_expl),
                    style = textStyle
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedExtractorActionButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.open_settings))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            RewardSnackbar()
        }
    }
}

private fun shopScreenConstraintSet() = ConstraintSet {
    val buyView = createRefFor(ViewIds.BUY_VIEW)
    val header = createRefFor(ViewIds.HEADER)
    val alternative = createRefFor(ViewIds.ALTERNATIVE)
    val status = createRefFor(ViewIds.STATUS)
    val searches = createRefFor(ViewIds.SEARCHES)

    constrain(header) {
        start.linkTo(parent.start)
        top.linkTo(parent.top, margin = 8.dp)
    }

    constrain(status) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
        top.linkTo(header.bottom, margin = 24.dp)
    }

    constrain(searches) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
        top.linkTo(status.bottom, margin = 24.dp)
    }

    constrain(alternative) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(buyView.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(buyView) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(searches.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val STATUS = "status_view"
    const val ALTERNATIVE = "alternative"
    const val BUY_VIEW = "buy_view"
    const val HEADER = "header_view"
    const val SEARCHES = "searches_view"
}
