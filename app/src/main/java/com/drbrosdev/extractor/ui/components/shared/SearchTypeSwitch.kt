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
import androidx.compose.ui.unit.sp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.asStringRes
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@Composable
fun SearchTypeSwitch(
    modifier: Modifier = Modifier,
    selection: SearchType,
    onSelectionChanged: (SearchType) -> Unit,
    enabled: Boolean = true,
) {
    Column {
        Text(
            text = stringResource(id = R.string.match_keyword),
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
                        selected = selection == item,
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
        SearchTypeSwitch(
            selection = SearchType.PARTIAL,
            onSelectionChanged = {}
        )
    }
}