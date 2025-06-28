package com.drbrosdev.extractor.ui.imageinfo.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drbrosdev.extractor.R

// visual
@Immutable
data class VisualAnnotationsState(
    val keywords: List<String>,
    val onDelete: (String) -> Unit
)

@Composable
fun VisualKeywordsEditor(
    modifier: Modifier = Modifier,
    state: VisualAnnotationsState
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.visual_embeddings),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        KeywordFlowRow(
            values = state.keywords,
            onClick = { state.onDelete(it) }
        )
    }
}

@Composable
fun KeywordFlowRow(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    values: List<String>,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        values.forEach {
            InputChip(
                selected = false,
                onClick = { onClick(it) },
                label = { Text(it) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "clear keyword"
                    )
                }
            )
        }
    }
}
