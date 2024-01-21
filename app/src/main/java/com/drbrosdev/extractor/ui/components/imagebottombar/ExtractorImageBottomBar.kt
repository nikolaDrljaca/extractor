package com.drbrosdev.extractor.ui.components.imagebottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun ExtractorImageBottomBar(
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
            .padding(horizontal = 24.dp, vertical = 12.dp)
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
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = barItem.iconRes),
                    contentDescription = "",
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

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        ExtractorImageBottomBar(onClick = {})
    }
}