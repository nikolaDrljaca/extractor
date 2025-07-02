package com.drbrosdev.extractor.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.search.stringRes
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.painterRes
import com.drbrosdev.extractor.ui.components.extractorlabelfilter.stringRes
import com.drbrosdev.extractor.ui.components.searchsheet.ExtractorSearchSheetComponent
import com.drbrosdev.extractor.ui.components.searchsheet.dateRange
import com.drbrosdev.extractor.ui.dialog.datepicker.ExtractorDatePicker
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import com.drbrosdev.extractor.util.asDisplayString

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
        modifier = modifier,
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
    val dateRange by remember {
        derivedStateOf {
            state.dateRange()
        }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.date),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            ToggleButton(
                checked = dateRange?.start != null,
                onCheckedChange = { onClick() },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
            ) {
                Text(text = dateRange?.start.asDisplayString(stringResource(id = R.string.start_date)))
            }

            ToggleButton(
                checked = dateRange?.end != null,
                onCheckedChange = { onClick() },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
            ) {
                Text(text = dateRange?.end.asDisplayString(stringResource(id = R.string.end_date)))
            }

            FilledIconButton(onClick = onReset) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh date filter")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LupaFilterSheet(
    modifier: Modifier = Modifier,
    component: ExtractorSearchSheetComponent
) {
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
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp, start = 4.dp)
            )
            LupaKeywordTypeFilter(
                selectedKeyword = component.keywordType,
                onChange = component::onKeywordTypeChange
            )

            LupaSearchTypeFilter(
                selected = component.searchType,
                onChange = component::onSearchTypeChange
            )

            LupaDateRangeFilter(
                onClick = component::showDateRangePicker,
                onReset = component::onResetDateSelection,
                state = component.dateRangePickerState
            )

            if (component.shouldShowDateRangePicker) {
                ExtractorDatePicker(
                    onDismiss = component::hideDateRangePicker,
                    onConfirm = component::onDateRangeConfirm,
                    modifier = Modifier,
                    state = component.dateRangePickerState
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
private fun CurrentPreview() {
    ExtractorTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            LupaFilterSheet(
                component = ExtractorSearchSheetComponent({}, SavedStateHandle())
            )

            LupaSearchFloatingBar { }
        }
    }
}