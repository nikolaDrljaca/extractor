package com.drbrosdev.extractor.ui.components.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R
import com.drbrosdev.extractor.ui.theme.ExtractorTheme
import kotlinx.coroutines.flow.Flow

class ExtractorSearchTypeSwitchState(
    initialSelection: Selection = Selection.PARTIAL
) {

    var selection by mutableStateOf(initialSelection)
        private set

    fun updateSelection(value: Selection) {
        selection = value
    }

    enum class Selection {
        FULL,
        PARTIAL
    }

    companion object {

        fun Saver() =
            androidx.compose.runtime.saveable.Saver<ExtractorSearchTypeSwitchState, Selection>(
                save = { it.selection },
                restore = {
                    ExtractorSearchTypeSwitchState(
                        initialSelection = it
                    )
                }
            )
    }
}

fun ExtractorSearchTypeSwitchState.Selection.asString(): String {
    return when (this) {
        ExtractorSearchTypeSwitchState.Selection.FULL -> "Full"
        ExtractorSearchTypeSwitchState.Selection.PARTIAL -> "Partial"
    }
}

fun ExtractorSearchTypeSwitchState.selectionFlow(): Flow<ExtractorSearchTypeSwitchState.Selection> {
    return snapshotFlow { this.selection }
}

@Composable
fun rememberExtractorSearchTypeSwitchState(
    initial: ExtractorSearchTypeSwitchState.Selection
): ExtractorSearchTypeSwitchState {
    return rememberSaveable(saver = ExtractorSearchTypeSwitchState.Saver()) {
        ExtractorSearchTypeSwitchState(initial)
    }
}

private val selectionItems = ExtractorSearchTypeSwitchState.Selection.entries.toList()

@Composable
fun ExtractorSearchTypeSwitch(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    state: ExtractorSearchTypeSwitchState
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
            selectionItems.forEach { item ->
                FilterChip(
                    selected = state.selection == item,
                    onClick = {
                        state.updateSelection(item)
                    },
                    label = { Text(text = item.asString()) },
                    leadingIcon = {},
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
                        selected = state.selection == item,
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
        ExtractorSearchTypeSwitch(
            state = ExtractorSearchTypeSwitchState(
                ExtractorSearchTypeSwitchState.Selection.PARTIAL
            )
        )
    }
}