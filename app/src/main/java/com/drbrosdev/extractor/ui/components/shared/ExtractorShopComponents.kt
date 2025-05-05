package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorShopItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(size = 12.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .then(modifier)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    4.dp,
                    alignment = Alignment.Bottom
                ),
                horizontalAlignment = Alignment.Start,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$ 0.99", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "500 Searches",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.rounded_shop_24),
                contentDescription = "Play Store 500 searches"
            )
        }
    }
}

@Composable
fun ExtractorShopPlaceholder(
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(size = 12.dp),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .then(modifier)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Coming soon!", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Billing features are still a work in progress. For now, look at the alternative way to get more searches.",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 30.dp)
        ) {
            ExtractorShopItem(onClick = {})
            ExtractorShopPlaceholder()
        }
    }
}