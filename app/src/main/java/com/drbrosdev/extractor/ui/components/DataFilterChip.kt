package com.drbrosdev.extractor.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R

sealed class DataFilterChip(
    open val label: String,
    @DrawableRes open val resId: Int
) {
    data class All(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : DataFilterChip(label, resId)

    data class Text(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : DataFilterChip(label, resId)

    data class Image(
        override val label: String,
        @DrawableRes override val resId: Int
    ) : DataFilterChip(label, resId)
}

val chipItems = listOf(
    DataFilterChip.All(label = "All", resId = R.drawable.round_tag_24),
    DataFilterChip.Text(label = "Text", resId = R.drawable.round_tag_24),
    DataFilterChip.Image(label = "Image", resId = R.drawable.round_tag_24),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFilter(
    modifier: Modifier = Modifier,
    onFilterChanged: (DataFilterChip) -> Unit
) {
    var selected by remember {
        mutableIntStateOf(0)
    }

    Column {
        Text(
            text = "Data Type",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Normal
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                            tint = Color.White
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = Color.Black,
                        labelColor = Color.White,
                        selectedLabelColor = Color.White,
                        disabledLeadingIconColor = Color.White
                    )
                )
            }
        }
    }
}
