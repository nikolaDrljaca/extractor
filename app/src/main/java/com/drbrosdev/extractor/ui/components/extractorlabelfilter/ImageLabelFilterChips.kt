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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme


private val chipItems = listOf(
    ImageLabelFilterChipData.All(label = "All", resId = R.drawable.round_tag_24),
    ImageLabelFilterChipData.Text(label = "Text", resId = R.drawable.round_text_fields_24),
    ImageLabelFilterChipData.Image(label = "Image", resId = R.drawable.round_image_search_24),
)

@Composable
fun ImageLabelFilterChips(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    initial: Int = 0,
    onFilterChanged: (ImageLabelFilterChipData) -> Unit
) {
    var selected by rememberSaveable {
        mutableIntStateOf(initial)
    }

    Column {
        Text(
            text = "Embedding Type",
            style = MaterialTheme.typography.titleLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.Normal
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
                    selected = selected == index,
                    onClick = {
                        selected = index
                        onFilterChanged(chipItems[index])
                    },
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
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White,
                        labelColor = contentColor,
                        selectedLeadingIconColor = Color.White,
                        iconColor = contentColor
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected == index,
                        borderColor = Color.White
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
        ImageLabelFilterChips(onFilterChanged = {})
    }
}
