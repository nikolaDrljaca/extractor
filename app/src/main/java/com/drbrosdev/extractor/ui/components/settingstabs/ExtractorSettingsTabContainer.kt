package com.drbrosdev.extractor.ui.components.settingstabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorSettingsTabContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(SettingsTabItem) -> Unit,
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                ExtractorTabIndicator(
                    Modifier.tabIndicatorOffset(it[selectedIndex]),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            },
            divider = {},
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            tabItems.forEachIndexed { index, _ ->
                val tabText = settingsItemAsString(item = tabItems[index])
                Tab(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .zIndex(1f),
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    selectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Text(
                        text = tabText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        content(tabItems[selectedIndex])
    }
}


@Composable
private fun ExtractorTabIndicator(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(CircleShape)
            .zIndex(0f)
            .background(color = color)
    )
}
