package com.drbrosdev.extractor.ui.components.extractorlicenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsItem
import com.drbrosdev.extractor.ui.components.extractorsettings.ExtractorSettingsItemPosition
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtractorLicenses(
    modifier: Modifier = Modifier,
    onClick: (link: String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.then(modifier)
    ) {
        allLicenses.forEachIndexed { index, license ->
            val itemPosition = when (index) {
                0 -> ExtractorSettingsItemPosition.FIRST
                allLicenses.size - 1 -> ExtractorSettingsItemPosition.LAST
                else -> ExtractorSettingsItemPosition.NORMAL
            }

            ExtractorSettingsItem(
                onClick = { onClick(license.link) },
                itemPosition = itemPosition,
                trailingSlot = {
                    Icon(
                        painter = painterResource(id = R.drawable.round_link_24),
                        contentDescription = "Anchor link",
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(text = license.name, style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = license.link,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme(dynamicColor = false) {
        Column {
            ExtractorLicenses(onClick = {})
        }
    }
}
