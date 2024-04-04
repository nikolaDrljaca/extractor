package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.asStringRes
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun ExtractorSearchTypeSwitch(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    selection: SearchType,
    onSelectionChanged: (SearchType) -> Unit,
    enabled: Boolean = true,
) {

    Column {
        Text(
            text = stringResource(id = R.string.match_keyword),
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
            SearchType.entries.toList().forEach { item ->
                FilterChip(
                    selected = selection == item,
                    onClick = {
                              onSelectionChanged(item)
                    },
                    label = { Text(text = stringResource(id = item.asStringRes())) },
                    leadingIcon = {},
                    enabled = enabled,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White,
                        labelColor = contentColor,
                        selectedLeadingIconColor = Color.White,
                        iconColor = contentColor,
                        disabledContainerColor = Color.Transparent,
                        disabledLabelColor = Color.Gray,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = enabled,
                        selected = selection == item,
                        borderColor = contentColor,
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
        ExtractorSearchTypeSwitch(
            selection = SearchType.PARTIAL,
            onSelectionChanged = {}
        )
    }
}