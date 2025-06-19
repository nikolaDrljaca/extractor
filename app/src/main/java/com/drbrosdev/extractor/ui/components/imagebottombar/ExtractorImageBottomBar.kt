package com.drbrosdev.extractor.ui.components.imagebottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem.EDIT
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem.EX_INFO
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem.SHARE
import com.drbrosdev.extractor.ui.components.imagebottombar.ExtractorBottomBarItem.USE_AS
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.CombinedPreview


@Composable
private fun ExtractorImageBottomBar2(
    onClick: (ExtractorBottomBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .zIndex(1f)
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .then(modifier),
    ) {
        ExtractorBottomBarItem.entries.forEachIndexed { _, barItem ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClick(barItem) }
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = barItem.iconRes),
                    contentDescription = stringResource(barItem.stringRes),
                    modifier = Modifier.requiredSize(32.dp),
                    tint = Color.White
                )

                Text(
                    text = stringResource(id = barItem.stringRes),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExtractorImageBottomBar(
    onClick: (ExtractorBottomBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = Modifier
            .then(modifier),
        contentPadding = PaddingValues(6.dp),
        expandedShadowElevation = 2.dp,
        collapsedShadowElevation = 2.dp
    ) {
        ExtractorBottomBarItem.entries.forEach { barItem ->
            when (barItem) {
                SHARE, EDIT, USE_AS -> IconButton(onClick = { onClick(barItem) }) {
                    Icon(
                        painter = painterResource(id = barItem.iconRes),
                        contentDescription = stringResource(barItem.stringRes),
                    )
                }

                EX_INFO -> FilledIconButton(
                    onClick = { onClick(barItem) },
                    modifier = Modifier.width(64.dp)
                ) {
                    Icon(
                        painter = painterResource(id = barItem.iconRes),
                        contentDescription = stringResource(barItem.stringRes),
                    )
                }
            }
        }
    }
}

@CombinedPreview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Surface {
            Column {
                ExtractorImageBottomBar(onClick = {})
            }
        }
    }
}