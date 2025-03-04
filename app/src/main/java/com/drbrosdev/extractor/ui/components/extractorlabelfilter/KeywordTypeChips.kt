package com.drbrosdev.extractor.ui.components.extractorlabelfilter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


@Composable
fun KeywordTypeChips(
    modifier: Modifier = Modifier,
    selection: Int = 0,
    onFilterChanged: (KeywordTypeChipData) -> Unit,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val chipItems = remember {
        listOf(
            KeywordTypeChipData.All(
                label = context.getString(R.string.chip_all),
                resId = R.drawable.round_tag_24
            ),
            KeywordTypeChipData.Text(
                label = context.getString(R.string.chip_text),
                resId = R.drawable.round_text_fields_24
            ),
            KeywordTypeChipData.Image(
                label = context.getString(R.string.chip_image),
                resId = R.drawable.round_image_search_24
            ),
        )
    }

    Column {
        Text(
            text = stringResource(R.string.keyword_type),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp
            )
        )

        Row(
            modifier = Modifier
                .then(modifier),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            chipItems.forEachIndexed { index, item ->
                FilterChip(
                    selected = selection == index,
                    onClick = {
                        onFilterChanged(chipItems[index])
                    },
                    enabled = enabled,
                    label = { Text(text = item.label) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = item.resId),
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        iconColor = MaterialTheme.colorScheme.onSurface,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color.Gray,
                        disabledContainerColor = Color.Transparent
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = enabled,
                        selected = selection == index,
                        borderColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = Color.Gray,
                        disabledSelectedBorderColor = Color.Gray
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
        KeywordTypeChips(onFilterChanged = {})
    }
}
