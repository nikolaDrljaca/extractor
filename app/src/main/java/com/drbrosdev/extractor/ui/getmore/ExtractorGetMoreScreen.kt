package com.drbrosdev.extractor.ui.getmore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.shared.BackIconButton
import com.drbrosdev.extractor.ui.components.shared.ExtractorShopItem
import com.drbrosdev.extractor.ui.components.shared.OutlinedExtractorActionButton


@Composable
fun ExtractorGetMoreScreen(
    onBack: () -> Unit,
    onViewAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Normal
    )
    val smallLabel = MaterialTheme.typography.labelSmall.copy(
        color = Color.Gray
    )

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
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

        Column(
            modifier = Modifier.layoutId(ViewIds.AD_VIEW),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(text = stringResource(R.string.watch_an_ad), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = stringResource(R.string.ad_support),
                style = textStyle
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedExtractorActionButton(
                onClick = onViewAdClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_rewarded_ads_24),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "View Ad")
            }
        }

        Column(
            modifier = Modifier.layoutId(ViewIds.BUY_VIEW),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Text(text = stringResource(R.string.buy_more), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = stringResource(R.string.direct_support),
                style = textStyle
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(2) {
                    ExtractorShopItem(onClick = { /*TODO*/ })
                }
            }
        }

        Box(modifier = Modifier.layoutId(ViewIds.DISCLAIMER)) {
            Text(
                text = stringResource(R.string.buy_disclaimer),
                style = smallLabel
            )
        }
    }
}

private fun getMoreScreenConstraintSet() = ConstraintSet {
    val adsView = createRefFor(ViewIds.AD_VIEW)
    val buyView = createRefFor(ViewIds.BUY_VIEW)
    val header = createRefFor(ViewIds.HEADER)
    val disclaimer = createRefFor(ViewIds.DISCLAIMER)

    constrain(header) {
        start.linkTo(parent.start)
        top.linkTo(parent.top, margin = 8.dp)
    }

    constrain(adsView) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(header.bottom, margin = 16.dp)
        width = Dimension.fillToConstraints
    }

    constrain(buyView) {
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        top.linkTo(adsView.bottom, margin = 24.dp)
        width = Dimension.fillToConstraints
    }

    constrain(disclaimer) {
        bottom.linkTo(parent.bottom, margin = 8.dp)
        start.linkTo(parent.start, margin = 16.dp)
        end.linkTo(parent.end, margin = 16.dp)
        width = Dimension.fillToConstraints
    }
}

private object ViewIds {
    const val AD_VIEW = "ad_view"
    const val BUY_VIEW = "buy_view"
    const val HEADER = "header_view"
    const val DISCLAIMER = "disc_view"
}
