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
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R

@Composable
fun ExtractorShopItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(size = 12.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary,
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
                contentDescription = ""
            )
        }
    }
}