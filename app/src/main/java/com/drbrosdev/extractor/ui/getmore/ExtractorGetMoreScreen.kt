package com.drbrosdev.extractor.ui.getmore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.drbrosdev.extractor.ui.components.shared.ExtractorShopItem
import com.drbrosdev.extractor.ui.components.shared.ExtractorViewAdContainer


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtractorGetMoreScreen(
    onBack: () -> Unit,
    onViewAdClick: () -> Unit,
    onPurchaseItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState,
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )
    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .verticalScroll(scrollState),
        constraintSet = getMoreScreenConstraintSet()
    ) {
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

        ExtractorViewAdContainer(
            modifier = Modifier.layoutId(ViewIds.AD_VIEW)
        )

        Column(
            modifier = Modifier.layoutId(ViewIds.BUY_VIEW),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.buy_more),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.direct_support),
                style = textStyle
            )

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 2
            ) {
                repeat(4) {
                    ExtractorShopItem(
                        onClick = onPurchaseItemClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.buy_disclaimer),
                style = smallLabel
            )
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.layoutId(ViewIds.SNACKBAR)
        ) {
            RewardSnackbar()
        }
    }
}

private fun getMoreScreenConstraintSet() = ConstraintSet {
    val adsView = createRefFor(ViewIds.AD_VIEW)
    val buyView = createRefFor(ViewIds.BUY_VIEW)
    val header = createRefFor(ViewIds.HEADER)
    val snackbar = createRefFor(ViewIds.SNACKBAR)

    constrain(snackbar) {
        bottom.linkTo(parent.bottom, margin = 24.dp)
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(header) {
        start.linkTo(parent.start)
        top.linkTo(parent.top, margin = 8.dp)
    }

    constrain(adsView) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(header.bottom, margin = 4.dp)
        width = Dimension.fillToConstraints
    }

    constrain(buyView) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(adsView.bottom, margin = 24.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val AD_VIEW = "ad_view"
    const val BUY_VIEW = "buy_view"
    const val HEADER = "header_view"
    const val SNACKBAR = "get_more_snackbar"
}
