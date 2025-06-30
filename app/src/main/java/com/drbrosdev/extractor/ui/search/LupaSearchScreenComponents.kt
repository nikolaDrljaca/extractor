package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.stringRes
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.painterRes
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.stringRes
import com.drbrosdev.extractor.ui.theme.ExtractorTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Bar() {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    // Apply a floatingToolbarVerticalNestedScroll Modifier to the Column to toggle
                    // the expanded state of the HorizontalFloatingToolbar.
                    .floatingToolbarVerticalNestedScroll(
                        expanded = expanded,
                        onExpand = { expanded = true },
                        onCollapse = { expanded = false },
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = remember { LoremIpsum().values.first() })
            }
            HorizontalFloatingToolbar(
                expanded = expanded,
                floatingActionButton = {
                    // Match the FAB to the vibrantColors. See also StandardFloatingActionButton.
                    FloatingToolbarDefaults.VibrantFloatingActionButton(
                        onClick = { /* doSomething() */ }
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = -ScreenOffset, y = -ScreenOffset),
                colors = vibrantColors,
                content = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Person, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Localized description")
                    }
                },
            )
        }
    }
}

sealed interface LupaSearchFloatingBarEvent {
    data object OnSearch : LupaSearchFloatingBarEvent

    data object OnAdd : LupaSearchFloatingBarEvent

    data object OnFilter : LupaSearchFloatingBarEvent

    data object OnReset : LupaSearchFloatingBarEvent
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LupaSearchFloatingBar(
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onEvent: (LupaSearchFloatingBarEvent) -> Unit
) {
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()

    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            // Match the FAB to the vibrantColors. See also StandardFloatingActionButton.
            FloatingToolbarDefaults.VibrantFloatingActionButton(
                onClick = { onEvent(LupaSearchFloatingBarEvent.OnSearch) }
            ) {
                Icon(Icons.Filled.Search, "Search")
            }
        },
        modifier = modifier
            .offset(x = -ScreenOffset, y = -ScreenOffset),
        colors = vibrantColors,
        content = {
            IconButton(onClick = { onEvent(LupaSearchFloatingBarEvent.OnReset) }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
            }
            IconButton(onClick = { onEvent(LupaSearchFloatingBarEvent.OnAdd) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
            FilledIconButton(onClick = { onEvent(LupaSearchFloatingBarEvent.OnFilter) }) {
                Icon(
                    painterResource(R.drawable.rounded_filter_list_24),
                    contentDescription = "Filter"
                )
            }
        },
    )
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LupaKeywordTypeFilter(
    modifier: Modifier = Modifier,
    selectedKeyword: KeywordType,
    onChange: (KeywordType) -> Unit
) {
    val options = KeywordType.entries.toTypedArray()
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.keyword_type),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            options.forEach { keywordType ->
                ToggleButton(
                    checked = keywordType == selectedKeyword,
                    onCheckedChange = { onChange(keywordType) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                    shapes =
                        when (keywordType) {
                            KeywordType.ALL -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            KeywordType.IMAGE -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            KeywordType.TEXT -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                ) {
                    Icon(
                        painter = painterResource(keywordType.painterRes()),
                        contentDescription = "Localized description",
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(stringResource(keywordType.stringRes()))
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LupaSearchTypeFilter(
    modifier: Modifier = Modifier,
    selected: SearchType,
    onChange: (SearchType) -> Unit
) {
    val options = SearchType.entries.toTypedArray()
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.match_keyword),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            options.forEach { searchType ->
                ToggleButton(
                    checked = searchType == selected,
                    onCheckedChange = { onChange(searchType) },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                    shapes =
                        when (searchType) {
                            SearchType.FULL -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            SearchType.PARTIAL -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        },
                ) {
                    Text(stringResource(searchType.stringRes()))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LupaDateRangeFilter(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onReset: () -> Unit,
    state: DateRangePickerState
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.match_keyword),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            ToggleButton(
//                checked = searchType == selected,
                checked = false,
                onCheckedChange = { onClick() },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
            ) {
                Text("Start Date")
            }

            ToggleButton(
//                checked = searchType == selected,
                checked = false,
                onCheckedChange = { onClick() },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
            ) {
                Text("End date")
            }

            FilledIconButton(onClick = onReset) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh date filter")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LupaFilterSheet(modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LupaKeywordTypeFilter(
                selectedKeyword = KeywordType.ALL,
                onChange = {}
            )

            LupaSearchTypeFilter(
                selected = SearchType.PARTIAL,
                onChange = {}
            )

            LupaDateRangeFilter(
                onClick = {},
                onReset = {},
                state = rememberDateRangePickerState()
            )
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            LupaFilterSheet()
        }
    }
}